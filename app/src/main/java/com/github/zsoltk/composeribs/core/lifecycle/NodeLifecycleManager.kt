package com.github.zsoltk.composeribs.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.RoutingKey
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
        destroyRemovedChildren()
    }

    private fun manageChildrenLifecycle() {
        val routingSource = host.routingSource ?: return
        lifecycle.coroutineScope.launch {
            val lifecycleState = lifecycle.changesAsFlow()

            combine(
                lifecycleState,
                host.children(),
                ::Pair
            ).collect { (state, children) ->
                children.values.forEach { child ->
                    val maxLifecycle = routingSource.maxLifecycleState(child.key)
                    val current = minOf(state, maxLifecycle)
                    child.lazyNode?.changeState(current)
                }
            }
        }
    }

    private fun destroyRemovedChildren() {
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
                            scan.prev[key]?.lazyNode?.changeState(Lifecycle.State.DESTROYED)
                        }
                    }
                }
        }
    }

    fun changeState(state: Lifecycle.State) {
        if (lifecycle.currentState != state) {
            lifecycleRegistry.currentState = state
        }
        if (state == Lifecycle.State.DESTROYED) {
            // lifecycle.coroutineScope is closed at this moment
            // need to clean up children as they are out of sync now
            host.children().value.values.forEach { it.lazyNode?.changeState(state) }
        }
    }

    private class ScanResult<Routing>(
        val prev: Map<RoutingKey<Routing>, Node.ChildEntry<Routing>>? = null,
        val current: Map<RoutingKey<Routing>, Node.ChildEntry<Routing>>? = null,
    )

}
