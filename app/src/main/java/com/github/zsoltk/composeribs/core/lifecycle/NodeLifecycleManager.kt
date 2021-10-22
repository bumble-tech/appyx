package com.github.zsoltk.composeribs.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import com.github.zsoltk.composeribs.core.children.ChildEntry
import com.github.zsoltk.composeribs.core.children.ChildEntryMap
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

/**
 * Hosts [LifecycleRegistry] to manage the current node lifecycle
 * and updates lifecycle of children nodes when updated.
 */
internal class NodeLifecycleManager<Routing>(
    private val host: NodeLifecycleManagerHost<Routing>,
) {

    private val lifecycleRegistry = LifecycleRegistry(host.lifecycleOwner)

    val lifecycle: Lifecycle
        get() = lifecycleRegistry

    init {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun observe() {
        manageChildrenLifecycle()
        updateRemovedChildren()
    }

    private fun manageChildrenLifecycle() {
        val routingSource = host.routingSource ?: return
        lifecycle.coroutineScope.launch {
            val lifecycleState = lifecycle.changesAsFlow()

            // observe both routingSource and children
            combine(
                lifecycleState,
                routingSource.all,
                host.children(),
                ::Triple
            ).collect { (state, elements, children) ->
                elements.forEach { element ->
                    val maxLifecycle =
                        if (routingSource.isOnScreen(element.key)) Lifecycle.State.RESUMED
                        else Lifecycle.State.CREATED
                    val current = minOf(state, maxLifecycle)
                    when (val child = children[element.key]) {
                        is ChildEntry.Eager -> child.node.updateLifecycleState(current)
                        is ChildEntry.Lazy -> Unit
                    }
                }
            }
        }
    }

    private fun updateRemovedChildren() {
        lifecycle.coroutineScope.launch {
            host
                .children()
                .scan(ScanResult<Routing>()) { previous, current ->
                    ScanResult(previous.current, current)
                }
                .collect { scan ->
                    if (scan.prev != null && scan.current != null) {
                        val removedKeys = scan.prev.keys - scan.current.keys
                        removedKeys.forEach { key ->
                            val removedChild = scan.prev[key]
                            if (removedChild is ChildEntry.Eager) {
                                removedChild.node.updateLifecycleState(Lifecycle.State.DESTROYED)
                            }
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
            host.children().value.values.forEach { child ->
                if (child is ChildEntry.Eager) {
                    child.node.updateLifecycleState(Lifecycle.State.DESTROYED)
                }
            }
        }
    }

    private class ScanResult<Routing>(
        val prev: ChildEntryMap<Routing>? = null,
        val current: ChildEntryMap<Routing>? = null,
    )

}
