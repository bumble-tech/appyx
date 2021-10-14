package com.github.zsoltk.composeribs.core

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler


@Composable
fun <T, S> Subtree(
    routingSource: RoutingSource<T, S>,
    block: @Composable SubtreeScope<T, S>.() -> Unit
) {
    block(SubtreeScope(routingSource))
}

@Composable
fun <T, S> Subtree(
    routingSource: RoutingSource<T, S>,
    transitionHandler: TransitionHandler<S>,
    block: @Composable SubtreeTransitionScope<T, S>.() -> Unit
) {
    block(SubtreeTransitionScope(routingSource, transitionHandler))
}

class SubtreeScope<T, S>(
    val routingSource: RoutingSource<T, S>
) {

    @Composable
    inline fun <reified V : T> Node<T>.visibleChildren(
        block: @Composable (child: @Composable () -> Unit) -> Unit,
    ) {
        // TODO consider instead of Node receiver
//        val node = LocalNode.current?.let { it as Node<T> }
//            ?: error("Subtree can't be invoked outside of a Node's Composable context")

        val rsOnScreen = this@SubtreeScope.routingSource.onScreen

        val onScreen = remember(rsOnScreen) {
            rsOnScreen
                .filter { it.key.routing is V || it.key.routing!!::class.java.isAssignableFrom(V::class.java) }
                .map { childOrCreate(it.key) }
        }

        onScreen.forEach { childEntry ->
            key(childEntry.key) {
                block(
                    child = { childEntry.node.Compose() }
                )
            }
        }
    }

    @Composable
    inline fun <reified V : T> Node<T>.children(
        block: @Composable (child: @Composable () -> Unit, routingElement: RoutingElement<T, S>) -> Unit,
    ) {
        // TODO consider instead of Node receiver
//        val node = LocalNode.current?.let { it as Node<T> }
//            ?: error("Subtree can't be invoked outside of a Node's Composable context")

        val all = this@SubtreeScope.routingSource.all
        val children =
//            remember(all) { // TODO proper key for remember
            all
                .filter { it.key.routing is V || it.key.routing!!::class.java.isAssignableFrom(V::class.java) }
                .map { childOrCreate(it.key) to it }
//        }

        children.forEach { (childEntry, routingElement) ->
            key(childEntry.key) {
                block(
                    child = { childEntry.node.Compose() },
                    routingElement = routingElement,
                )
            }
        }
    }
}

class SubtreeTransitionScope<T, S>(
    val routingSource: RoutingSource<T, S>,
    val transitionHandler: TransitionHandler<S>,
) {

    @Composable
    inline fun <reified V : T> Node<T>.children(
        block: @Composable ChildTransitionScope<S>.(transitionModifier: Modifier, child: @Composable () -> Unit) -> Unit,
    ) {
//        // TODO consider
//        val node = LocalNode.current?.let { it as Node<T> }
//            ?: error("Subtree can't be invoked outside of a Node's Composable context")

        val onScreen =
//            remember {
            this@SubtreeTransitionScope.routingSource.onScreen
                .filter { it.key.routing is V || it.key.routing!!::class.java.isAssignableFrom(V::class.java) }
                .map { it to childOrCreate(it.key) }
//        }

        onScreen.forEach { (routingElement, childEntry) ->
            key(childEntry.key) {
                val transitionScope =
                    transitionHandler.handle(
                        fromState = routingElement.fromState,
                        toState = routingElement.targetState,
                        onTransitionFinished = {
                            this@SubtreeTransitionScope.routingSource.onTransitionFinished(
                                childEntry.key
                            )
                        })

                transitionScope.block(
                    transitionModifier = transitionScope.transitionModifier,
                    child = { childEntry.node.Compose() },
                )
            }
        }
    }
}

interface ChildTransitionScope<S> {
    val transition: Transition<S>
    val transitionModifier: Modifier
}

internal class ChildTransitionScopeImpl<S>(
    override val transition: Transition<S>,
    override val transitionModifier: Modifier
) : ChildTransitionScope<S>
