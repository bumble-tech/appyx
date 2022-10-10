package com.bumble.appyx.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import com.bumble.appyx.core.children.ChildEntry
import com.bumble.appyx.core.children.ChildEntryMap
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.NavModel
import com.bumble.appyx.core.navigation.NavModelAdapter
import com.bumble.appyx.core.withPrevious
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable

/**
 * Hosts [LifecycleRegistry] to manage the current node lifecycle
 * and updates lifecycle of children nodes when updated.
 */
internal class ChildNodeLifecycleManager<NavTarget>(
    private val navModel: NavModel<NavTarget, *>,
    private val children: StateFlow<ChildEntryMap<NavTarget>>,
    private val coroutineScope: CoroutineScope,
) {

    private val lifecycleState = MutableStateFlow(Lifecycle.State.INITIALIZED)
    private val portalKeysState = MutableStateFlow<List<NavKey<NavTarget>>>(mutableListOf())

    fun pushPortal(key: NavKey<NavTarget>) {
        portalKeysState.update { list ->
            val newList = list.toMutableList()
            newList.add(key)
            newList
        }
    }

    fun removePortal() {
        // TODO remove by key
        portalKeysState.update { list ->
            val newList = list.toMutableList()
            newList.removeLast()
            newList
        }
    }

    /**
     * Propagates the parent lifecycle to children.
     *
     * Child lifecycles should be updated only after all lifecycle callbacks are invoked in the current node.
     *
     * **DO NOT USE LIFECYCLE.OBSERVE HERE!**
     *
     * Otherwise a child node lifecycle might be updated before the parent.
     * It leads to incorrect registration order of back handlers.
     */
    fun propagateLifecycleToChildren(state: Lifecycle.State) {
        lifecycleState.value = state
    }

    fun launch() {
        // previously state management was split into multiple jobs
        // but we found execution order issues when the main thread is busy (?)
        // so we merged it into single one
        coroutineScope.launch {
            combine(
                lifecycleState,
                navModel.screenState.keys(),
                children.withPrevious(),
                portalKeysState,
                ::Quadruple
            )
                .onCompletion {
                    // when scope is closed we need to destroy all existing children
                    // do it manually here as emit() does not work for cancellation case
                    children
                        .value
                        .values
                        .forEach { entry -> entry.setState(Lifecycle.State.DESTROYED) }
                }
                .collect { (parentLifecycleState, screenState, children, portals) ->

                    if (portals.isEmpty()) {
                        screenState.onScreen.forEach { key ->
                            val childState = minOf(parentLifecycleState, Lifecycle.State.RESUMED)
                            children.current[key]?.setState(childState)
                        }

                        screenState.offScreen.forEach { key ->
                            val childState = minOf(parentLifecycleState, Lifecycle.State.CREATED)
                            children.current[key]?.setState(childState)
                        }
                    } else {
                        val lastPortalKey = portals.last()

                        (screenState.onScreen + screenState.offScreen - lastPortalKey).forEach { key ->
                            val childState = minOf(parentLifecycleState, Lifecycle.State.CREATED)
                            children.current[key]?.setState(childState)
                        }

                        children.current[lastPortalKey]?.setState(
                            minOf(
                                parentLifecycleState,
                                Lifecycle.State.RESUMED
                            )
                        )
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

    private fun <NavTarget> Flow<NavModelAdapter.ScreenState<NavTarget, *>>.keys() =
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

    data class Quadruple<out A, out B, out C, out D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
    ) : Serializable {

        /**
         * Returns string representation of the [Triple] including its [first], [second] and [third] values.
         */
        override fun toString(): String = "($first, $second, $third)"
    }

    private data class ScreenState<NavTarget>(
        val onScreen: List<NavKey<NavTarget>> = emptyList(),
        val offScreen: List<NavKey<NavTarget>> = emptyList(),
    )

}
