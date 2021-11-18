package com.github.zsoltk.composeribs.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import com.github.zsoltk.composeribs.core.children.ChildEntryMap
import com.github.zsoltk.composeribs.core.children.nodeOrNull
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.withPrevious
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Hosts [LifecycleRegistry] to manage the current node lifecycle
 * and updates lifecycle of children nodes when updated.
 */
class ChildNodeLifecycleManager<Routing>(
    private val lifecycle: Lifecycle,
    private val routingSource: RoutingSource<Routing, *>,
    private val children: StateFlow<ChildEntryMap<Routing>>,
    private val coroutineScope: CoroutineScope = lifecycle.coroutineScope,
) {

    fun launch() {
        updateChildrenWhenDestroyed()
        manageChildrenLifecycle()
        updateRemovedChildren()
    }

    private fun updateChildrenWhenDestroyed() {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                // lifecycle.coroutineScope is closed at this moment
                // need to clean up children as they are out of sync now
                children.value.values.forEach { child ->
                    child.nodeOrNull?.updateLifecycleState(Lifecycle.State.DESTROYED)
                }
            }
        })
    }

    private fun manageChildrenLifecycle() {
        coroutineScope.launch {
            val lifecycleState = lifecycle.changesAsFlow()

            // observe both routingSource and children
            combine(
                lifecycleState,
                routingSource.elements,
                children,
                ::Triple
            ).collect { (state, elements, children) ->
                elements.forEach { element ->
                    val maxLifecycle = Lifecycle.State.CREATED
                    val current = minOf(state, maxLifecycle)
                    children[element.key]?.nodeOrNull?.updateLifecycleState(current)
                }
            }
        }
    }

    private fun updateRemovedChildren() {
        coroutineScope.launch {
            children
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

}
