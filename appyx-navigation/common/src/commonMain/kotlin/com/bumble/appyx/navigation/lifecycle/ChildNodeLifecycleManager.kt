package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.interactions.core.model.AppyxComponent
import com.bumble.appyx.navigation.children.ChildEntry
import com.bumble.appyx.navigation.children.ChildEntryMap
import com.bumble.appyx.navigation.children.nodeOrNull
import com.bumble.appyx.navigation.withPrevious
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

/**
 * Hosts [PlatformLifecycleRegistry] to manage the current node lifecycle
 * and updates lifecycle of children nodes when updated.
 */
internal class ChildNodeLifecycleManager<InteractionTarget : Any>(
    private val appyxComponent: AppyxComponent<InteractionTarget, *>,
    private val children: StateFlow<ChildEntryMap<InteractionTarget>>,
    private val keepMode: ChildEntry.KeepMode,
    private val coroutineScope: CoroutineScope,
) {

    private val lifecycleState = MutableStateFlow(Lifecycle.State.INITIALIZED)

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
                appyxComponent.elements,
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
                        if (keepMode == ChildEntry.KeepMode.KEEP) {
                            val childState = minOf(parentLifecycleState, Lifecycle.State.CREATED)
                            children.current[key]?.setState(childState)
                        } else {
                            // Look up in the previous because in the current it is already suspended
                            // and does not have a reference to the node
                            children.previous?.get(key)?.setState(Lifecycle.State.DESTROYED)
                        }
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

    private fun ChildEntry<*>.setState(state: Lifecycle.State) {
        nodeOrNull?.updateLifecycleState(state)
    }

}
