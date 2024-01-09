package com.bumble.appyx.navigation.children

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.state.MutableSavedStateMap
import com.bumble.appyx.navigation.modality.AncestryInfo
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.build
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
internal class ChildNodeCreationManager<ChildReference : Any>(
    private var savedStateMap: SavedStateMap?,
    private val customisations: NodeCustomisationDirectory,
    private val keepMode: ChildEntry.KeepMode,
) {
    private lateinit var parentNode: ParentNode<ChildReference>
    private val _children =
        MutableStateFlow<Map<Element<ChildReference>, ChildEntry<ChildReference>>>(emptyMap())
    val children: StateFlow<ChildEntryMap<ChildReference>> = _children.asStateFlow()

    fun launch(parentNode: ParentNode<ChildReference>) {
        this.parentNode = parentNode
        savedStateMap.restoreChildren()?.also { restoredMap ->
            _children.update { restoredMap }
            savedStateMap = null
        }
        syncAppyxComponentWithChildren(parentNode)
    }

    private fun syncAppyxComponentWithChildren(parentNode: ParentNode<ChildReference>) {
        parentNode.lifecycle.coroutineScope.launch {
            parentNode.appyxComponent.elements.collect { state ->
                val appyxComponentKeepKeys: Set<Element<ChildReference>>
                val appyxComponentSuspendKeys: Set<Element<ChildReference>>
                val appyxComponentKeys: Set<Element<ChildReference>>
                when (keepMode) {
                    ChildEntry.KeepMode.KEEP -> {
                        appyxComponentKeepKeys =
                            (state.onScreen + state.offScreen)
                        appyxComponentSuspendKeys = emptySet()
                        appyxComponentKeys = appyxComponentKeepKeys
                    }

                    ChildEntry.KeepMode.SUSPEND -> {
                        appyxComponentKeepKeys =
                            state.onScreen
                        appyxComponentSuspendKeys =
                            state.offScreen
                        appyxComponentKeys = appyxComponentKeepKeys + appyxComponentSuspendKeys
                    }
                }
                updateChildren(
                    appyxComponentKeys,
                    appyxComponentKeepKeys,
                    appyxComponentSuspendKeys
                )
            }
        }
    }

    private fun updateChildren(
        appyxComponentElements: Set<Element<ChildReference>>,
        appyxComponentKeepElements: Set<Element<ChildReference>>,
        appyxComponentSuspendElements: Set<Element<ChildReference>>,
    ) {
        _children.update { map ->
            val localElements = map.keys
            val localKeptElements = map.entries.mapNotNullToSet { entry ->
                entry.key.takeIf { entry.value is ChildEntry.Initialized }
            }
            val localSuspendedKeys = map.entries.mapNotNullToSet { entry ->
                entry.key.takeIf { entry.value is ChildEntry.Suspended }
            }
            val newElements = appyxComponentElements - localElements
            val removedElements = localElements - appyxComponentElements
            val keepElements = localSuspendedKeys.intersect(appyxComponentKeepElements)
            val suspendElements = localKeptElements.intersect(appyxComponentSuspendElements)
            val noKeysChanges = newElements.isEmpty() && removedElements.isEmpty()
            val noSuspendChanges = keepElements.isEmpty() && suspendElements.isEmpty()
            if (noKeysChanges && noSuspendChanges) {
                return@update map
            }
            val mutableMap = map.toMutableMap()
            newElements.forEach { key ->
                val shouldSuspend = keepMode == ChildEntry.KeepMode.SUSPEND
                        && appyxComponentSuspendElements.contains(key)
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
    fun childOrCreate(element: Element<ChildReference>): ChildEntry.Initialized<ChildReference> {
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

    private fun SavedStateMap?.restoreChildren(): ChildEntryMap<ChildReference>? =
        (this?.get(KEY_CHILDREN_STATE) as? Map<Element<ChildReference>, SavedStateMap>)?.mapValues {
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
        key: Element<ChildReference>,
        savedState: SavedStateMap?,
        suspended: Boolean,
    ): ChildEntry<ChildReference> =
        if (suspended) {
            ChildEntry.Suspended(key, savedState)
        } else {
            ChildEntry.Initialized(
                key = key,
                node = parentNode
                    .buildChildNode(key.interactionTarget, childBuildContext(savedState))
                    .build()
            )
        }

    private fun ChildEntry<ChildReference>.initialize(): ChildEntry.Initialized<ChildReference> =
        when (this) {
            is ChildEntry.Initialized -> this
            is ChildEntry.Suspended ->
                ChildEntry.Initialized(
                    key = key,
                    node = parentNode.buildChildNode(
                        navTarget = key.interactionTarget,
                        buildContext = childBuildContext(savedState),
                    ).build()
                )
        }

    @Suppress("ForbiddenComment")
    private fun ChildEntry<ChildReference>.suspend(): ChildEntry.Suspended<ChildReference> =
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
