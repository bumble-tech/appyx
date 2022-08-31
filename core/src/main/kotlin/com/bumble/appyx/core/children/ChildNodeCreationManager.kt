package com.bumble.appyx.core.children

import androidx.lifecycle.coroutineScope
import com.bumble.appyx.core.modality.AncestryInfo
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.RoutingKey
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

internal class ChildNodeCreationManager<Routing : Any>(
    private var savedStateMap: SavedStateMap?,
    private val customisations: NodeCustomisationDirectory,
    private val childMode: ChildEntry.ChildMode,
) {
    private val _children =
        MutableStateFlow<Map<RoutingKey<Routing>, ChildEntry<Routing>>>(emptyMap())
    val children: StateFlow<ChildEntryMap<Routing>> = _children.asStateFlow()
    private lateinit var parentNode: ParentNode<Routing>

    fun launch(parentNode: ParentNode<Routing>) {
        this.parentNode = parentNode
        savedStateMap.restoreChildren()?.also { restoredMap ->
            _children.update { restoredMap }
            savedStateMap = null
        }
        parentNode.lifecycle.coroutineScope.launch {
            parentNode.navModel.elements.collect { elements ->
                _children.update { map ->
                    val navModelKeys = elements
                        .mapTo(HashSet(elements.size, 1f)) { element -> element.key }
                    val localKeys = map.keys
                    val newKeys = navModelKeys - localKeys
                    val removedKeys = localKeys - navModelKeys
                    val mutableMap = map.toMutableMap()
                    newKeys.forEach { key ->
                        mutableMap[key] =
                            childEntry(
                                key = key,
                                savedState = null,
                                suspended = childMode == ChildEntry.ChildMode.LAZY,
                            )
                    }
                    removedKeys.forEach { key ->
                        mutableMap.remove(key)
                    }
                    mutableMap
                }
            }
        }
    }

    @Suppress("ForbiddenComment")
    fun childOrCreate(routingKey: RoutingKey<Routing>): ChildEntry.Initialized<Routing> {
        // TODO: Should not allow child creation and throw exception instead to avoid desynchronisation
        val value = _children.value
        val child = value[routingKey] ?: error(
            "Rendering and children management is out of sync: requested $routingKey but have only ${value.keys}"
        )
        return when (child) {
            is ChildEntry.Initialized ->
                child
            is ChildEntry.Suspended ->
                _children.updateAndGet { map ->
                    val updateChild = map[routingKey]
                        ?: error("Requested child $routingKey disappeared")
                    when (updateChild) {
                        is ChildEntry.Initialized -> map
                        is ChildEntry.Suspended -> {
                            val initialized = ChildEntry.Initialized(
                                key = updateChild.key,
                                node = parentNode.resolve(
                                    routing = updateChild.key.routing,
                                    buildContext = childBuildContext(child.savedState),
                                ).build()
                            )
                            map.plus(routingKey to initialized)
                        }
                    }
                }[routingKey] as ChildEntry.Initialized
        }
    }

    private fun SavedStateMap?.restoreChildren(): ChildEntryMap<Routing>? =
        (this?.get(KEY_CHILDREN_STATE) as? Map<RoutingKey<Routing>, SavedStateMap>)?.mapValues {
            childEntry(it.key, it.value, childMode == ChildEntry.ChildMode.LAZY)
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
        key: RoutingKey<Routing>,
        savedState: SavedStateMap?,
        suspended: Boolean,
    ): ChildEntry<Routing> =
        if (suspended) {
            ChildEntry.Suspended(key, savedState)
        } else {
            ChildEntry.Initialized(
                key = key,
                node = parentNode
                    .resolve(key.routing, childBuildContext(savedState))
                    .build()
            )
        }

    private companion object {
        const val KEY_CHILDREN_STATE = "ChildrenState"
    }

}
