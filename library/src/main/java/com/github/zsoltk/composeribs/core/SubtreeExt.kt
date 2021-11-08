package com.github.zsoltk.composeribs.core

import androidx.compose.animation.core.Transition
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionParams
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass


@Composable
fun <T, S> Subtree(
    routingSource: RoutingSource<T, S>,
    block: @Composable SubtreeScope<T, S>.() -> Unit
) {
    block(SubtreeScope(routingSource))
}

@Composable
fun <T : Any, S> Subtree(
    modifier: Modifier,
    routingSource: RoutingSource<T, S>,
    transitionHandler: TransitionHandler<S>,
    block: @Composable SubtreeTransitionScope<T, S>.() -> Unit,
) {
    BoxWithConstraints(modifier) {
        block(
            SubtreeTransitionScope(
                routingSource,
                transitionHandler,
                TransitionParams(
                    bounds = TransitionBounds(
                        width = maxWidth,
                        height = maxHeight
                    )
                )
            )
        )
    }
}

class SubtreeScope<T, S>(
    val routingSource: RoutingSource<T, S>
) {

    @Composable
    inline fun <reified V : T> ParentNode<T>.visibleChildren(
        block: @Composable (child: @Composable () -> Unit) -> Unit,
    ) {
        // TODO consider instead of Node receiver
//        val node = LocalNode.current?.let { it as Node<T> }
//            ?: error("Subtree can't be invoked outside of a Node's Composable context")

        val children by this@SubtreeScope.routingSource.onScreen
            .map { state ->
                state
                    .elements
                    .filter { it.key.routing is V }
                    .map { childOrCreate(it.key) }
            }
            .collectAsState(initial = emptyList())

        children.forEach { childEntry ->
            key(childEntry.key) {
                block(
                    child = { childEntry.node.Compose() }
                )
            }
        }
    }

    @Composable
    inline fun <reified V : T> ParentNode<T>.children(
        block: @Composable (child: @Composable () -> Unit, routingElement: RoutingElement<T, S>) -> Unit,
    ) {
        // TODO consider instead of Node receiver
//        val node = LocalNode.current?.let { it as Node<T> }
//            ?: error("Subtree can't be invoked outside of a Node's Composable context")

        val children by this@SubtreeScope.routingSource.all
            .map {
                it.elements.filter { it.key.routing is V }
                    .map { it to childOrCreate(it.key) }
            }
            .collectAsState(initial = emptyList())

        children.forEach { (routingElement, childEntry) ->
            key(childEntry.key) {
                block(
                    child = { childEntry.node.Compose() },
                    routingElement = routingElement,
                )
            }
        }
    }
}

class SubtreeTransitionScope<T : Any, S>(
    val routingSource: RoutingSource<T, S>,
    val transitionHandler: TransitionHandler<S>,
    private val transitionParams: TransitionParams,
) {

    @Composable
    inline fun <reified V : T> ParentNode<T>.children(
        noinline block: @Composable ChildTransitionScope<S>.(child: @Composable () -> Unit) -> Unit,
    ) {
        children(V::class, block)
    }

    @Composable
    fun ParentNode<T>.children(
        clazz: KClass<out T>,
        block: @Composable ChildTransitionScope<S>.(child: @Composable () -> Unit) -> Unit,
    ) {
//        // TODO consider
//        val node = LocalNode.current?.let { it as Node<T> }
//            ?: error("Subtree can't be invoked outside of a Node's Composable context")

        val children by this@SubtreeTransitionScope.routingSource.onScreen
            .map { state ->
                state
                    .elements
                    .filter { clazz.isInstance(it.key.routing) }
                    .map { it to childOrCreate(it.key) }
            }
            .collectAsState(initial = emptyList())

        val saveableStateHolder = rememberSaveableStateHolder()
        children.forEach { (routingElement, childEntry) ->
            key(childEntry.key) {
                saveableStateHolder.SaveableStateProvider(key = routingElement.key) {
                    val transitionScope =
                        transitionHandler.handle(
                            transitionParams = transitionParams,
                            fromState = routingElement.fromState,
                            toState = routingElement.targetState,
                            onTransitionFinished = {
                                this@SubtreeTransitionScope.routingSource.onTransitionFinished(
                                    childEntry.key
                                )
                            })

                    transitionScope.block(
                        child = {
                            CompositionLocalProvider(LocalTransitionModifier provides transitionScope.transitionModifier) {
                                childEntry.node.Compose()
                            }
                        }
                    )
                }
            }
        }
    }
}

val LocalTransitionModifier = compositionLocalOf<Modifier?> { null }

interface ChildTransitionScope<S> {
    val transition: Transition<S>
    val transitionModifier: Modifier
}

internal class ChildTransitionScopeImpl<S>(
    override val transition: Transition<S>,
    override val transitionModifier: Modifier
) : ChildTransitionScope<S>
