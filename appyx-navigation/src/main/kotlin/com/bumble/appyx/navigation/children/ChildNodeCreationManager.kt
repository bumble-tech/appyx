package com.bumble.appyx.navigation.children

import androidx.lifecycle.coroutineScope
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.navigation.modality.AncestryInfo
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.build
import com.bumble.appyx.navigation.state.MutableSavedStateMap
import com.bumble.appyx.navigation.state.SavedStateMap
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

/**
 * Initializes and removes nodes based on parent node's navModel.
 *
 * Lifecycle of these nodes is managed in [com.bumble.appyx.core.lifecycle.ChildNodeLifecycleManager].
 */
internal class ChildNodeCreationManager<NavTarget : Any>(
    private var savedStateMap: SavedStateMap?,
    private val customisations: NodeCustomisationDirectory,
    private val keepMode: ChildEntry.KeepMode,
) {
    private lateinit var parentNode: ParentNode<NavTarget>
    private val _children =
        MutableStateFlow<Map<Element<NavTarget>, ChildEntry<NavTarget>>>(emptyMap())
    val children: StateFlow<ChildEntryMap<NavTarget>> = _children.asStateFlow()

    fun launch(parentNode: ParentNode<NavTarget>) {
        this.parentNode = parentNode
        savedStateMap.restoreChildren()?.also { restoredMap ->
            _children.update { restoredMap }
            savedStateMap = null
        }
        syncNavModelWithChildren(parentNode)
    }


    private fun syncNavModelWithChildren(parentNode: ParentNode<NavTarget>) {
        parentNode.lifecycle.coroutineScope.launch {
            parentNode.interactionModel.screenState.collect { state ->
                val navModelKeepKeys: Set<Element<NavTarget>>
                val navModelSuspendKeys: Set<Element<NavTarget>>
                val navModelKeys: Set<Element<NavTarget>>
                when (keepMode) {
                    ChildEntry.KeepMode.KEEP -> {
                        navModelKeepKeys =
                            (state.onScreen + state.offScreen)
                        navModelSuspendKeys = emptySet()
                        navModelKeys = navModelKeepKeys
                    }
                    ChildEntry.KeepMode.SUSPEND -> {
                        navModelKeepKeys =
                            state.onScreen
                        navModelSuspendKeys =
                            state.offScreen
                        navModelKeys = navModelKeepKeys + navModelSuspendKeys
                    }
                }
                updateChildren(navModelKeys, navModelKeepKeys, navModelSuspendKeys)
            }
        }
    }

//    private fun syncNavModelWithChildren(parentNode: ParentNode<NavTarget>) {
//        parentNode.lifecycle.coroutineScope.launch {
//
//            val navModelKeepElements: Set<NavElement<NavTarget>> =
//                parentNode.interactionModel.availableElements()
//
//            val navModelElements: Set<NavElement<NavTarget>> = navModelKeepElements
//
//            updateChildren(navModelElements, navModelKeepElements, emptySet())
//        }
//    }

    private fun updateChildren(
        navModelElements: Set<Element<NavTarget>>,
        navModelKeepElements: Set<Element<NavTarget>>,
        navModelSuspendElements: Set<Element<NavTarget>>,
    ) {
        _children.update { map ->
            val localElements = map.keys
            val localKeptElements = map.entries.mapNotNullToSet { entry ->
                entry.key.takeIf { entry.value is ChildEntry.Initialized }
            }
            val localSuspendedKeys = map.entries.mapNotNullToSet { entry ->
                entry.key.takeIf { entry.value is ChildEntry.Suspended }
            }
            val newElements = navModelElements - localElements
            val removedElements = localElements - navModelElements
            val keepElements = localSuspendedKeys.intersect(navModelKeepElements)
            val suspendElements = localKeptElements.intersect(navModelSuspendElements)
            val noKeysChanges = newElements.isEmpty() && removedElements.isEmpty()
            val noSuspendChanges = keepElements.isEmpty() && suspendElements.isEmpty()
            if (noKeysChanges && noSuspendChanges) {
                return@update map
            }
            val mutableMap = map.toMutableMap()
            newElements.forEach { key ->
                val shouldSuspend =
                    keepMode == ChildEntry.KeepMode.SUSPEND && navModelSuspendElements.contains(key)
                mutableMap[key] =
                    childEntry(
                        key = key,
                        savedState = null,
                        suspended = shouldSuspend,
                    )
            }
            removedElements.forEach { key ->
                mutableMap.remove(key)
            }
            keepElements.forEach { key ->
                mutableMap[key] = requireNotNull(mutableMap[key]).initialize()
            }
            suspendElements.forEach { key ->
                mutableMap[key] = requireNotNull(mutableMap[key]).suspend()
            }
            mutableMap
        }
    }

    @Suppress("ForbiddenComment")
    fun childOrCreate(element: Element<NavTarget>): ChildEntry.Initialized<NavTarget> {
        // TODO: Should not allow child creation and throw exception instead to avoid desynchronisation
        val value = _children.value
        val child = value[element] ?: error(
            "Rendering and children management is out of sync: requested $element but have only ${value.keys}"
        )
        return when (child) {
            is ChildEntry.Initialized ->
                child
            is ChildEntry.Suspended ->
                _children.updateAndGet { map ->
                    val updateChild = map[element]
                        ?: error("Requested child $element disappeared")
                    when (updateChild) {
                        is ChildEntry.Initialized -> map
                        is ChildEntry.Suspended ->
                            map.plus(element to updateChild.initialize())
                    }
                }[element] as ChildEntry.Initialized
        }
    }

    private fun SavedStateMap?.restoreChildren(): ChildEntryMap<NavTarget>? =
        (this?.get(KEY_CHILDREN_STATE) as? Map<Element<NavTarget>, SavedStateMap>)?.mapValues {
            // Always restore in suspended mode, they will be unsuspended or destroyed on the first sync cycle
            childEntry(it.key, it.value, true)
        }

    fun saveChildrenState(writer: MutableSavedStateMap) {
        val children = _children.value
        if (children.isNotEmpty()) {
            val childrenState =
                children
                    .mapValues { (_, entry) ->
                        when (entry) {
                            is ChildEntry.Initialized -> entry.node.saveInstanceState(writer.saverScope)
                            is ChildEntry.Suspended -> entry.savedState
                        }
                    }
            if (childrenState.isNotEmpty()) {
                writer[KEY_CHILDREN_STATE] = childrenState
            }
        }
    }

    private fun childBuildContext(savedState: SavedStateMap?): BuildContext =
        BuildContext(
            ancestryInfo = AncestryInfo.Child(parentNode),
            savedStateMap = savedState,
            customisations = customisations.getSubDirectoryOrSelf(parentNode::class),
        )

    private fun childEntry(
        key: Element<NavTarget>,
        savedState: SavedStateMap?,
        suspended: Boolean,
    ): ChildEntry<NavTarget> =
        if (suspended) {
            ChildEntry.Suspended(key, savedState)
        } else {
            ChildEntry.Initialized(
                key = key,
                node = parentNode
                    .resolve(key.navTarget, childBuildContext(savedState))
                    .build()
            )
        }

    private fun ChildEntry<NavTarget>.initialize(): ChildEntry.Initialized<NavTarget> =
        when (this) {
            is ChildEntry.Initialized -> this
            is ChildEntry.Suspended ->
                ChildEntry.Initialized(
                    key = key,
                    node = parentNode.resolve(
                        navTarget = key.navTarget,
                        buildContext = childBuildContext(savedState),
                    ).build()
                )
        }

    @Suppress("ForbiddenComment")
    private fun ChildEntry<NavTarget>.suspend(): ChildEntry.Suspended<NavTarget> =
        when (this) {
            is ChildEntry.Suspended -> this
            is ChildEntry.Initialized ->
                ChildEntry.Suspended(
                    key = key,
                    // TODO: Not able to get a scope from Compose here, providing fake one
                    savedState = node.saveInstanceState { true },
                )
        }

    private companion object {
        const val KEY_CHILDREN_STATE = "ChildrenState"

        private fun <T, R : Any> Collection<T>.mapNotNullToSet(mapper: (T) -> R?): Set<R> =
            mapNotNullTo(HashSet(size, 1f), mapper)
    }

}
