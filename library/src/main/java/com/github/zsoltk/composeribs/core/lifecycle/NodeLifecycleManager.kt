package com.github.zsoltk.composeribs.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import com.github.zsoltk.composeribs.core.Parent
import com.github.zsoltk.composeribs.core.children.nodeOrNull
import com.github.zsoltk.composeribs.core.withPrevious
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Hosts [LifecycleRegistry] to manage the current node lifecycle
 * and updates lifecycle of children nodes when updated.
 */
internal class NodeLifecycleManager<Routing>(
    private val parent: Parent<Routing>,
) {

    private val lifecycleRegistry = LifecycleRegistry(parent)

    val lifecycle: Lifecycle
        get() = lifecycleRegistry

    init {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun start() {
        manageChildrenLifecycle()
        updateRemovedChildren()
    }

    private fun manageChildrenLifecycle() {
        val routingSource = parent.routingSource ?: return
        lifecycle.coroutineScope.launch {
            val lifecycleState = lifecycle.changesAsFlow()

            // observe both routingSource and children
            combine(
                lifecycleState,
                routingSource.all,
                parent.children,
                ::Triple
            ).collect { (state, elements, children) ->
                elements.forEach { element ->
                    val maxLifecycle =
                        if (routingSource.isOnScreen(element.key)) Lifecycle.State.RESUMED
                        else Lifecycle.State.CREATED
                    val current = minOf(state, maxLifecycle)
                    children[element.key]?.nodeOrNull?.updateLifecycleState(current)
                }
            }
        }
    }

    private fun updateRemovedChildren() {
        lifecycle.coroutineScope.launch {
            parent
                .children
                .withPrevious()
                .collect { value ->
                    if (value.previous != null) {
                        val removedKeys = value.previous.keys - value.current.keys
                        removedKeys.forEach { key ->
                            val removedChild = value.previous[key]
                            removedChild?.nodeOrNull?.updateLifecycleState(Lifecycle.State.DESTROYED)
                        }
                    }
                }
        }
    }

    fun updateLifecycleState(state: Lifecycle.State) {
        if (lifecycle.currentState != state) {
            lifecycleRegistry.currentState = state
        }
        if (state == Lifecycle.State.DESTROYED) {
            // lifecycle.coroutineScope is closed at this moment
            // need to clean up children as they are out of sync now
            parent.children.value.values.forEach { child ->
                child.nodeOrNull?.updateLifecycleState(Lifecycle.State.DESTROYED)
            }
        }
    }

}
