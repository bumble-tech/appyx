package com.github.zsoltk.composeribs.core.composable

import androidx.compose.animation.core.Transition
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.children.ChildEntry
import com.github.zsoltk.composeribs.core.node.LocalNode
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingSourceAdapter
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionParams
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

@Composable
fun <T : Any, S> Subtree(
    adapter: RoutingSourceAdapter<T, S>,
    block: @Composable SubtreeScope<T, S>.() -> Unit
) {
    block(SubtreeScope(adapter))
}

class SubtreeScope<T : Any, S>(
    val adapter: RoutingSourceAdapter<T, S>,
) {

    @Composable
    inline fun <reified V : T> visibleChildren(): State<List<ChildEntry.Eager<T>>> {
        val node = LocalNode.current?.let { it as? ParentNode<T> }
            ?: error("Subtree can't be invoked outside of a ParentNode's Composable context. LocalNode.current is ${LocalNode.current}")
        return adapter.onScreen
            .map { list ->
                list
                    .filter { it.key.routing is V }
                    .map { node.childOrCreate(it.key) }
            }.collectAsState(initial = emptyList())
    }

    @Composable
    inline fun <reified V : T> ParentNode<T>.visibleChildren(
        block: @Composable (child: @Composable () -> Unit) -> Unit,
    ) {
        // TODO consider instead of Node receiver
//        val node = LocalNode.current?.let { it as Node<T> }
//            ?: error("Subtree can't be invoked outside of a Node's Composable context")

        val children by adapter.onScreen
            .map { list ->
                list
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
        block: @Composable (child: @Composable () -> Unit, routingElement: RoutingElement<T, out S>) -> Unit,
    ) {
        // TODO consider instead of Node receiver
//        val node = LocalNode.current?.let { it as Node<T> }
//            ?: error("Subtree can't be invoked outside of a Node's Composable context")

        val children by adapter.elements
            .map {
                it.filter { it.key.routing is V }
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

@Composable
fun <T : Any, S> Subtree(
    modifier: Modifier,
    transitionHandler: TransitionHandler<T, S>,
    adapter: RoutingSourceAdapter<T, S>,
    block: @Composable SubtreeTransitionScope<T, S>.() -> Unit
) {
    BoxWithConstraints(modifier) {
        block(
            SubtreeTransitionScope(
                transitionHandler,
                TransitionParams(
                    bounds = TransitionBounds(
                        width = maxWidth,
                        height = maxHeight
                    )
                ),
                adapter
            )
        )
    }
}

class SubtreeTransitionScope<T : Any, S>(
    private val transitionHandler: TransitionHandler<T, S>,
    private val transitionParams: TransitionParams,
    private val adapter: RoutingSourceAdapter<T, S>
) {

    @Composable
    inline fun <reified V : T> ParentNode<T>.children(
        noinline block: @Composable ChildTransitionScope<S>.(child: @Composable () -> Unit, transitionDescriptor: TransitionDescriptor<T, S>) -> Unit,
    ) {
        children(V::class, block)
    }

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
        _children(clazz) { scope, child, _ ->
            scope.block(
                child = child
            )
        }
    }

    @Composable
    fun ParentNode<T>.children(
        clazz: KClass<out T>,
        block: @Composable ChildTransitionScope<S>.(child: @Composable () -> Unit, transitionDescriptor: TransitionDescriptor<T, S>) -> Unit,
    ) {
        _children(clazz) { scope, child, descriptor ->
            scope.block(
                transitionDescriptor = descriptor,
                child = child,
            )
        }
    }

    @Composable
    private fun ParentNode<T>._children(
        clazz: KClass<out T>,
        block: @Composable (transitionScope: ChildTransitionScope<S>, child: @Composable () -> Unit, transitionDescriptor: TransitionDescriptor<T, S>) -> Unit
    ) {

        // TODO consider:
        //        val node = LocalNode.current?.let { it as Node<T> }
        //            ?: error("Subtree can't be invoked outside of a Node's Composable context")

        val children by adapter.onScreen
            .map { list ->
                list
                    .filter { clazz.isInstance(it.key.routing) }
                    .map { it to childOrCreate(it.key) }
            }
            .collectAsState(emptyList())

        val saveableStateHolder = rememberSaveableStateHolder()
        children.forEach { (routingElement, childEntry) ->
            key(childEntry.key.id) {
                saveableStateHolder.SaveableStateProvider(key = routingElement.key) {
                    val descriptor = routingElement.createDescriptor(transitionParams)
                    val transitionScope =
                        transitionHandler.handle(
                            descriptor = descriptor,
                            onTransitionFinished = {
                                adapter.onTransitionFinished(
                                    childEntry.key
                                )
                            })

                    block(
                        transitionDescriptor = descriptor,
                        transitionScope = transitionScope,
                        child = {
                            CompositionLocalProvider(LocalTransitionModifier provides transitionScope.transitionModifier) {
                                childEntry.node.Compose()
                            }
                        },
                    )
                }
            }
        }
    }


    private fun RoutingElement<T, out S>.createDescriptor(transitionParams: TransitionParams) =
        TransitionDescriptor(
            uid = key.id,
            params = transitionParams,
            operation = operation,
            element = key.routing,
            fromState = fromState,
            toState = targetState
        )
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
