package com.github.zsoltk.composeribs.core

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class Node<T>(
    private val view: RibView<T>,
    private val subtreeController: SubtreeController<T>? = null
) {
    private val children = mutableStateMapOf<RoutingKey<T>, ChildEntry<T>>()

    @SuppressLint("RememberReturnType")
    @Composable
    fun Compose() {
        subtreeController?.let {
            WithBackStack(it, it.backStack.elements)
//            with(it) {
//                LaunchedEffect(backStack.elements) {
//                    manageRemoved(children)
//                    manageOffScreen(children)
//                    manageOnScreen(children)
//                }
//            }
//
//            val composedViews = children
//                .filter { it.value.onScreen }
//                .mapValues {
//                    val node = it.value.node
//                    requireNotNull(node) { "Node for on-screen entry should have been resolved" }
//                    node.view
//                }
//
//            view.Compose(composedViews)

        } ?: run {
            view.Compose(emptyMap())
        }
    }

    @Composable
    private fun WithBackStack(
        subtreeController: SubtreeController<T>,
        elements: SnapshotStateList<BackStackElement<T>>
    ) {
        // FIXME
//        LaunchedEffect(elements) {
            subtreeController.manageRemoved(children)
            subtreeController.manageOffScreen(children)
            subtreeController.manageOnScreen(children)
//        }

        val composedViews = children
            .filter { it.value.onScreen }
            .mapValues {
                val node = it.value.node
                requireNotNull(node) { "Node for on-screen entry should have been resolved" }
                node.view
            }

        view.Compose(composedViews)
    }


    // TODO need a different mechanism, as this would also remove permanents (if any)
    private fun SubtreeController<T>.manageRemoved(children: MutableMap<RoutingKey<T>, ChildEntry<T>>) {
        children.keys.forEach { routingKey ->
            if (backStack.elements.firstOrNull { it.routingKey == routingKey} == null) {
                children.remove(routingKey)
            }
        }
    }

    private fun SubtreeController<T>.manageOffScreen(children: MutableMap<RoutingKey<T>, ChildEntry<T>>) {
        backStack.offScreen.forEach { element ->
            val routingKey = element.routingKey

            children[routingKey]?.let { entry ->
                if (entry.onScreen) {
                    children[routingKey] = entry.copy(onScreen = false)
                }
            } ?: run {
                children[routingKey] = ChildEntry(
                    routing = routingKey.routing,
                    onScreen = false
                )
            }
        }
    }

    private fun SubtreeController<T>.manageOnScreen(children: MutableMap<RoutingKey<T>, ChildEntry<T>>) {
        backStack.onScreen.forEach { element ->
            val routingKey = element.routingKey

            children[routingKey]?.let { entry ->
                if (!entry.onScreen) {
                    children[routingKey] = entry.copy(
                        onScreen = true,
                        node = entry.node ?: resolver(routingKey.routing)
                    )
                }
            } ?: run {
                children[routingKey] = ChildEntry(
                    routing = routingKey.routing,
                    node = resolver(routingKey.routing),
                    onScreen = true
                )
            }
        }
    }
}
