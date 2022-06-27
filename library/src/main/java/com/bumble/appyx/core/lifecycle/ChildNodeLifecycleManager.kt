package com.bumble.appyx.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.core.CompareValues
import com.bumble.appyx.core.children.ChildEntryMap
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.RoutingSource
import com.bumble.appyx.core.withPrevious
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

/**
 * Hosts [LifecycleRegistry] to manage the current node lifecycle
 * and updates lifecycle of children nodes when updated.
 */
internal class ChildNodeLifecycleManager<Routing>(
    private val lifecycle: Lifecycle,
    private val routingSource: RoutingSource<Routing, *>,
    private val children: StateFlow<ChildEntryMap<Routing>>,
    private val coroutineScope: CoroutineScope = lifecycle.coroutineScope,
) {

    fun launch() {
        // previously state management was split into multiple jobs
        // but we found execution order issues when the main thread is busy (?)
        // so we merged it into single one
        coroutineScope.launch {
            combine(
                lifecycle.asFlow(),
                routingSource.onScreen.keys(),
                routingSource.offScreen.keys(),
                children.withPrevious(),
                ::State
            )
                .onCompletion {
                    // when scope is closed we need to destroy all existing children
                    // to do it we emmit special value where we consider all children removed from routing source
                    val children = children.value
                    emit(
                        State(
                            lifecycleState = Lifecycle.State.DESTROYED,
                            onScreen = emptySet(),
                            offScreen = emptySet(),
                            children = CompareValues(children, emptyMap()),
                        )
                    )
                }
                .collect { state ->
                    // because onScreen and offScreen lists are updated independently
                    // the same key might exists in both lists
                    // to avoid lifecycle jumps we should exclude them from offScreen
                    val overlapKeys = state.onScreen.intersect(state.offScreen)

                    state.onScreen.forEach { key ->
                        val childState = minOf(state.lifecycleState, Lifecycle.State.RESUMED)
                        state.children.current[key]?.nodeOrNull?.updateLifecycleState(childState)
                    }

                    state.offScreen.forEach { key ->
                        if (key in overlapKeys) return@forEach
                        val childState = minOf(state.lifecycleState, Lifecycle.State.CREATED)
                        state.children.current[key]?.nodeOrNull?.updateLifecycleState(childState)
                    }

                    if (state.children.previous != null) {
                        val removedKeys = state.children.previous.keys - state.children.current.keys
                        removedKeys.forEach { key ->
                            val removedChild = state.children.previous[key]
                            removedChild?.nodeOrNull?.updateLifecycleState(Lifecycle.State.DESTROYED)
                        }
                    }
                }
        }
    }

    private fun StateFlow<RoutingElements<Routing, *>>.keys(): Flow<Set<RoutingKey<Routing>>> =
        this
            .map { list -> list.mapTo(HashSet(list.size, 1f)) { it.key } }
            .distinctUntilChanged()

    private class State<Routing>(
        val lifecycleState: Lifecycle.State,
        val onScreen: Set<RoutingKey<Routing>>,
        val offScreen: Set<RoutingKey<Routing>>,
        val children: CompareValues<ChildEntryMap<Routing>>,
    ) {
        override fun toString(): String = "($lifecycleState, $onScreen, $offScreen, $children)"
    }
}
