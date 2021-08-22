package com.github.zsoltk.composeribs.core

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Suppress("TransitionPropertiesLabel")
class Node<T>(
    private val view: RibView<T>,
    private val subtreeController: SubtreeController<T>? = null
) {
    private val children = mutableStateMapOf<RoutingKey<T>, NodeChildEntry<T>>()

    @SuppressLint("RememberReturnType")
    @Composable
    fun Compose() {
        subtreeController?.let {
            WithBackStack(it, it.backStack.elements)
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
            .mapValues { childEntry ->
                val node = childEntry.value.node
                requireNotNull(node) { "Node for on-screen entry should have been resolved" }

                val modifier = elements
                    .firstOrNull { it.routingKey == childEntry.key }
                    ?.let { backStackElement ->
                        val currentState = remember { MutableTransitionState(BackStack.TransitionState.ADDED) }
                        currentState.targetState = backStackElement.targetState
                        val transition: Transition<BackStack.TransitionState> = updateTransition(currentState)
                        val color = transition.animateColor(
                            transitionSpec = { tween(1500) },
                            targetValueByState = {
                                when (it) {
                                    BackStack.TransitionState.ADDED -> Color.Red
                                    BackStack.TransitionState.ACTIVE -> Color.Blue
                                    BackStack.TransitionState.INACTIVE -> Color.LightGray
                                    BackStack.TransitionState.REMOVED -> Color.DarkGray
                                }
                        })

                        Modifier.background(color = color.value)
                    } ?: Modifier

                // TODO optimise
                ViewChildEntry(
                    key = childEntry.key,
                    view = node.view,
                    modifier =  modifier
                )
            }

        view.Compose(composedViews)
    }

    // TODO need a different mechanism, as this would also remove permanents (if any)
    private fun SubtreeController<T>.manageRemoved(children: MutableMap<RoutingKey<T>, NodeChildEntry<T>>) {
        children.keys.forEach { routingKey ->
            if (backStack.elements.firstOrNull { it.routingKey == routingKey} == null) {
                children.remove(routingKey)
            }
        }
    }

    private fun SubtreeController<T>.manageOffScreen(children: MutableMap<RoutingKey<T>, NodeChildEntry<T>>) {
        backStack.offScreen.forEach { element ->
            val routingKey = element.routingKey

            children[routingKey]?.let { entry ->
                if (entry.onScreen) {
                    children[routingKey] = entry.copy(onScreen = false)
                }
            } ?: run {
                children[routingKey] = NodeChildEntry(
                    routing = routingKey.routing,
                    onScreen = false
                )
            }
        }
    }

    private fun SubtreeController<T>.manageOnScreen(children: MutableMap<RoutingKey<T>, NodeChildEntry<T>>) {
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
                children[routingKey] = NodeChildEntry(
                    routing = routingKey.routing,
                    node = resolver(routingKey.routing),
                    onScreen = true
                )
            }
        }
    }
}
