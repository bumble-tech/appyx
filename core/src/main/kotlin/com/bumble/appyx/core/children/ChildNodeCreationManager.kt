package com.bumble.appyx.core.children

import androidx.lifecycle.coroutineScope
import com.bumble.appyx.core.modality.AncestryInfo
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.RoutingKey
import com.bumble.appyx.core.node.ParentNode
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

    fun launch(parentNode: ParentNode<Routing>) {
        savedStateMap.restoreChildren(parentNode)?.also { restoredMap ->
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
                            ChildEntry.create(
                                key = key,
                                resolver = parentNode,
                                buildContext = null.toBuildContext(parentNode),
                                childMode = childMode,
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

    fun childOrCreate(routingKey: RoutingKey<Routing>): ChildEntry.Eager<Routing> {
        val value = _children.value
        val child = value[routingKey] ?: error(
            "Rendering and children management is out of sync: requested $routingKey but have only ${value.keys}"
        )
        return when (child) {
            is ChildEntry.Eager ->
                child
            is ChildEntry.Lazy ->
                _children.updateAndGet { map ->
                    val updateChild = map[routingKey]
                        ?: error("Requested child $routingKey disappeared")
                    when (updateChild) {
                        is ChildEntry.Eager -> map
                        is ChildEntry.Lazy -> map.plus(routingKey to updateChild.initialize())
                    }
                }[routingKey] as ChildEntry.Eager
        }
    }

    private fun SavedStateMap?.restoreChildren(parentNode: ParentNode<Routing>): ChildEntryMap<Routing>? =
        (this?.get(KEY_CHILDREN_STATE) as? Map<RoutingKey<Routing>, SavedStateMap>)?.mapValues {
            ChildEntry.create(it.key, parentNode, it.value.toBuildContext(parentNode), childMode)
        }

    private fun SavedStateMap?.toBuildContext(parentNode: ParentNode<Routing>): BuildContext =
        BuildContext(
            ancestryInfo = AncestryInfo.Child(parentNode),
            savedStateMap = this,
            customisations = customisations.getSubDirectoryOrSelf(parentNode::class)
        )

    fun saveChildrenState(writer: MutableSavedStateMap) {
        val children = _children.value
        if (children.isNotEmpty()) {
            val childrenState =
                children
                    .mapValues { (_, entry) ->
                        when (entry) {
                            is ChildEntry.Eager -> entry.node.saveInstanceState(writer.saverScope)
                            is ChildEntry.Lazy -> entry.buildContext.savedStateMap
                        }
                    }
            if (childrenState.isNotEmpty()) {
                writer[KEY_CHILDREN_STATE] = childrenState
            }
        }
    }

    private companion object {
        const val KEY_CHILDREN_STATE = "ChildrenState"
    }

}
