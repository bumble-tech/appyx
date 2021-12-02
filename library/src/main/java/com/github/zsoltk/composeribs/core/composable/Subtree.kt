package com.github.zsoltk.composeribs.core.composable

import androidx.compose.animation.core.Transition
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionParams
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

@Composable
fun <Routing : Any, State> Subtree(
    modifier: Modifier,
    routingSource: RoutingSource<Routing, State>,
    transitionHandler: TransitionHandler<Routing, State>,
    block: @Composable SubtreeTransitionScope<Routing, State>.() -> Unit
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
                routingSource
            )
        )
    }
}

class SubtreeTransitionScope<T : Any, S>(
    private val transitionHandler: TransitionHandler<T, S>,
    private val transitionParams: TransitionParams,
    private val routingSource: RoutingSource<T, S>
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
        val children by this@SubtreeTransitionScope.routingSource.onScreen
            .map { list ->
                list
                    .filter { clazz.isInstance(it.key.routing) }
            }
            .collectAsState(emptyList())

        val saveableStateHolder = rememberSaveableStateHolder()
        children.forEach { routingElement ->
            key(routingElement.key.id) {
                Child(
                    routingElement,
                    saveableStateHolder,
                    transitionParams,
                    transitionHandler,
                    block
                )
            }

        }
    }
}
