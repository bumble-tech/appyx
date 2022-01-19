package com.github.zsoltk.composeribs.core.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.github.zsoltk.composeribs.core.node.ParentNode
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
    val density = LocalDensity.current.density
    var transitionBounds by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by derivedStateOf {
        TransitionParams(
            bounds = TransitionBounds(
                width = Dp(transitionBounds.width / density),
                height = Dp(transitionBounds.height / density)
            )
        )
    }
    Box(modifier = modifier
        .onSizeChanged {
            transitionBounds = it
        }
    ) {
        block(
            SubtreeTransitionScope(
                transitionHandler = transitionHandler,
                transitionParams = transitionParams,
                routingSource = routingSource
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
