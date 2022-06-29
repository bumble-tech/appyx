package com.bumble.appyx.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.core.children.ChildEntry
import com.bumble.appyx.core.children.ChildEntryMap
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.RoutingSource
import com.bumble.appyx.core.routing.RoutingSourceAdapter
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
                routingSource.screenState.keys(),
                children.withPrevious(),
                ::Triple
            )
                .onCompletion {
                    // when scope is closed we need to destroy all existing children
                    // do it manually here as emit() does not work for cancellation case
                    children
                        .value
                        .values
                        .forEach { entry -> entry.setState(Lifecycle.State.DESTROYED) }
                }
                .collect { (parentLifecycleState, screenState, children) ->
                    screenState.onScreen.forEach { key ->
                        val childState = minOf(parentLifecycleState, Lifecycle.State.RESUMED)
                        children.current[key]?.setState(childState)
                    }

                    screenState.offScreen.forEach { key ->
                        val childState = minOf(parentLifecycleState, Lifecycle.State.CREATED)
                        children.current[key]?.setState(childState)
                    }

                    if (children.previous != null) {
                        val removedKeys = children.previous.keys - children.current.keys
                        removedKeys.forEach { key ->
                            val removedChild = children.previous[key]
                            removedChild?.setState(Lifecycle.State.DESTROYED)
                        }
                    }
                }
        }
    }

    private fun <Routing> Flow<RoutingSourceAdapter.ScreenState<Routing, *>>.keys() =
        this
            .map { screenState ->
                ScreenState(
                    onScreen = screenState.onScreen.map { it.key },
                    offScreen = screenState.offScreen.map { it.key },
                )
            }
            .distinctUntilChanged()

    private fun ChildEntry<*>.setState(state: Lifecycle.State) {
        nodeOrNull?.updateLifecycleState(state)
    }

    private data class ScreenState<Routing>(
        val onScreen: List<RoutingKey<Routing>> = emptyList(),
        val offScreen: List<RoutingKey<Routing>> = emptyList(),
    )

}
