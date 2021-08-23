package com.github.zsoltk.composeribs.core

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.SubtreeController

@Suppress("TransitionPropertiesLabel")
class Node<T>(
    private val view: RibView<T>,
    private val subtreeController: SubtreeController<T, *>? = null
) {
    private val children = mutableMapOf<RoutingKey<T>, NodeChildEntry<T>>()
    private val keys = mutableStateListOf<RoutingKey<T>>()

    init {
        subtreeController?.routingSource?.onRemoved {
            children.remove(it)
            keys.remove(it)
        }
    }

    @SuppressLint("RememberReturnType")
    @Composable
    fun Compose() {
        subtreeController?.let {
            WithBackStack(it)
        } ?: run {
            view.Compose(emptyList())
        }
    }

    @Composable
    private fun WithBackStack(
        subtreeController: SubtreeController<T, *>
    ) {
        // FIXME
//        LaunchedEffect(elements) {
            subtreeController.manageOffScreen()
            subtreeController.manageOnScreen()
//        }

        val composedViews = keys
            .map { children[it]!! }
            .filter { it.onScreen }
            .map { childEntry ->
                key(childEntry.key) {
                    val node = childEntry.node
                    requireNotNull(node) { "Node for on-screen entry should have been resolved" }

                    val modifier = subtreeController.getModifierSnapshot(
                        key = childEntry.key
                    )

                    // TODO optimise (only update modifier, don't create new object)
                    ViewChildEntry(
                        key = childEntry.key,
                        view = node.view,
                        modifier =  modifier
                    )
                }
            }

        view.Compose(composedViews)
    }

    private fun SubtreeController<T, *>.manageOffScreen() {
        routingSource.offScreen.forEach { element ->
            val routingKey = element.key

            children[routingKey]?.let { entry ->
                if (entry.onScreen) {
                    children[routingKey] = entry.copy(onScreen = false)
                }
            } ?: run {
                val entry = NodeChildEntry(
                    key = routingKey,
                    onScreen = false
                )
                children[routingKey] = entry
                keys.add(routingKey)
            }
        }
    }

    private fun SubtreeController<T, *>.manageOnScreen() {
        routingSource.onScreen.forEach { element ->
            val routingKey = element.key

            children[routingKey]?.let { entry ->
                if (!entry.onScreen) {
                    children[routingKey] = entry.copy(
                        onScreen = true,
                        node = entry.node ?: resolver(routingKey.routing)
                    )
                }
            } ?: run {
                val entry = NodeChildEntry(
                    key = routingKey,
                    node = resolver(routingKey.routing),
                    onScreen = true
                )

                children[routingKey] = entry
                keys.add(routingKey)
            }
        }
    }
}
