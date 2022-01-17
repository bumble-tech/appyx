package com.github.zsoltk.composeribs.core.children

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.github.zsoltk.composeribs.core.lifecycle.isDestroyed
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.withPrevious
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class ChildAwareImpl : ChildAware {

    private val callbacks: MutableList<ChildAwareCallbackInfo> = ArrayList()

    private lateinit var children: StateFlow<Map<out RoutingKey<*>, ChildEntry<*>>>
    private lateinit var lifecycle: Lifecycle
    private lateinit var coroutineScope: CoroutineScope

    override lateinit var node: Node
        private set

    override fun init(node: Node) {
        this.node = node
        lifecycle = node.lifecycle
        coroutineScope = lifecycle.coroutineScope
        if (node is ParentNode<*>) {
            children = node.children
            initialize()
        } else {
            children = MutableStateFlow(emptyMap())
        }
    }

    private fun initialize() {
        coroutineScope.apply {
            launch { findNewKeys() }
            launch { findPromotedChildren() }
        }
    }

    private suspend fun findNewKeys() {
        children
            .withPrevious()
            .collect { value ->
                val addedKeys = value.current.keys - (value.previous?.keys ?: emptySet())
                if (addedKeys.isEmpty()) return@collect
                val currentNodes = getCreatedNodes(value.current)
                val visitedSet = HashSet<Node>()
                addedKeys.forEach { key ->
                    val node = value.current[key]?.nodeOrNull
                    if (node != null) {
                        notifyWhenChanged(node, currentNodes, visitedSet)
                        visitedSet.add(node)
                    }
                }
            }
    }

    private suspend fun findPromotedChildren() {
        children
            .withPrevious()
            .collect { value ->
                if (value.previous == null) return@collect
                val commonKeys = value.current.keys.intersect(value.previous.keys)
                if (commonKeys.isEmpty()) return@collect
                val nodes = getCreatedNodes(value.current)
                val visitedSet = HashSet<Node>()
                commonKeys.forEach { key ->
                    val current = value.current[key]
                    if (current != value.previous[key] && current is ChildEntry.Eager) {
                        notifyWhenChanged(current.node, nodes, visitedSet)
                        visitedSet.add(current.node)
                    }
                }
            }
    }

    override fun <T : Node> whenChildAttached(
        child: KClass<T>,
        callback: ChildCallback<T>
    ) {
        if (lifecycle.isDestroyed) return
        val info = ChildAwareCallbackInfo.Single(child, callback, lifecycle)
        callbacks += info
        notifyWhenRegistered(info)
        lifecycle.removeWhenDestroyed(info)
    }

    override fun <T1 : Node, T2 : Node> whenChildrenAttached(
        child1: KClass<T1>,
        child2: KClass<T2>,
        callback: ChildrenCallback<T1, T2>
    ) {
        if (lifecycle.isDestroyed) return
        val info = ChildAwareCallbackInfo.Double(child1, child2, callback, lifecycle)
        callbacks += info
        notifyWhenRegistered(info)
        lifecycle.removeWhenDestroyed(info)
    }

    private fun notifyWhenRegistered(callback: ChildAwareCallbackInfo) {
        callback.onRegistered(getCreatedNodes(children.value))
    }

    private fun notifyWhenChanged(child: Node, nodes: List<Node>, ignore: Set<Node>) {
        for (callback in callbacks) {
            when (callback) {
                is ChildAwareCallbackInfo.Double<*, *> -> callback.onNewNodeAppeared(
                    activeNodes = nodes,
                    newNode = child,
                    ignoreNodes = ignore,
                )
                is ChildAwareCallbackInfo.Single<*> -> callback.onNewNodeAppeared(child)
            }
        }
    }

    private fun Lifecycle.removeWhenDestroyed(info: ChildAwareCallbackInfo) {
        addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                callbacks.remove(info)
            }
        })
    }

    private fun getCreatedNodes(childEntryMap: Map<out RoutingKey<*>, ChildEntry<*>>) =
        childEntryMap.values.mapNotNull { entry ->
            when (entry) {
                is ChildEntry.Eager -> entry.node
                is ChildEntry.Lazy -> null
            }
        }

}
