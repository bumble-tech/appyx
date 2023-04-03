package com.bumble.appyx.core.children

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.lifecycle.MinimumCombinedLifecycle
import com.bumble.appyx.core.lifecycle.isDestroyed
import com.bumble.appyx.core.node.Node
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.safeCast

internal sealed class ChildAwareCallbackInfo {

    abstract fun onRegistered(activeNodes: List<Node>)

    class Single<T : Any>(
        private val child: KClass<T>,
        private val callback: ChildCallback<T>,
        private val parentLifecycle: Lifecycle,
    ) : ChildAwareCallbackInfo() {

        fun onNewNodeAppeared(newNode: Node) {
            if (parentLifecycle.isDestroyed) return
            val castedNode = child.safeCast(newNode)
            if (castedNode != null) {
                val lifecycle =
                    MinimumCombinedLifecycle(
                        parentLifecycle,
                        newNode.lifecycle,
                    ).lifecycle
                callback(lifecycle, castedNode)
            }
        }

        override fun onRegistered(activeNodes: List<Node>) {
            activeNodes.forEach { node ->
                onNewNodeAppeared(node)
            }
        }

    }

    class Double<T1 : Any, T2 : Any>(
        private val child1: KClass<T1>,
        private val child2: KClass<T2>,
        private val callback: ChildrenCallback<T1, T2>,
        private val parentLifecycle: Lifecycle,
    ) : ChildAwareCallbackInfo() {

        fun onNewNodeAppeared(
            activeNodes: Collection<Node>,
            newNode: Node,
            ignoreNodes: Set<Node>,
        ) {
            val second = getOther(newNode) ?: return
            activeNodes
                .filter { second.isInstance(it) && it != newNode && it !in ignoreNodes }
                .forEach { notify(newNode, it) }
        }

        override fun onRegistered(activeNodes: List<Node>) {
            activeNodes.forEachIndexed { index, node ->
                onNewNodeAppeared(
                    // Do not include already handled nodes to avoid call duplication
                    activeNodes = activeNodes.subList(index + 1, activeNodes.size),
                    newNode = node,
                    ignoreNodes = emptySet(),
                )
            }
        }

        private fun notify(node1: Node, node2: Node) {
            if (parentLifecycle.isDestroyed) return
            val lifecycle =
                MinimumCombinedLifecycle(
                    parentLifecycle,
                    node1.lifecycle,
                    node2.lifecycle,
                ).lifecycle
            if (child1.isInstance(node1)) {
                callback(lifecycle, child1.cast(node1), child2.cast(node2))
            } else {
                callback(lifecycle, child1.cast(node2), child2.cast(node1))
            }
        }

        private fun getOther(node: Node): KClass<*>? =
            when {
                child1.isInstance(node) -> child2
                child2.isInstance(node) -> child1
                else -> null
            }

    }

}
