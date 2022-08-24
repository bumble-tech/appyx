package com.bumble.appyx.core.composable

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
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.navigation.NavModel
import com.bumble.appyx.core.navigation.transition.JumpToEndTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionBounds
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionParams
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

@Composable
inline fun <reified Routing : Any, State : Any> ParentNode<Routing>.Children(
    navModel: NavModel<Routing, State>,
    modifier: Modifier = Modifier,
    transitionHandler: TransitionHandler<Routing, State> = remember { JumpToEndTransitionHandler() },
    noinline block: @Composable ChildrenTransitionScope<Routing, State>.() -> Unit = {
        children<Routing> { child ->
            child()
        }
    }
) {
    val density = LocalDensity.current.density
    var transitionBounds by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by remember(transitionBounds) {
        derivedStateOf {
            TransitionParams(
                bounds = TransitionBounds(
                    width = Dp(transitionBounds.width / density),
                    height = Dp(transitionBounds.height / density)
                )
            )
        }
    }
    Box(modifier = modifier
        .onSizeChanged {
            transitionBounds = it
        }
    ) {
        block(
            ChildrenTransitionScope(
                transitionHandler = transitionHandler,
                transitionParams = transitionParams,
                navModel = navModel
            )
        )
    }
}

class ChildrenTransitionScope<T : Any, S>(
    private val transitionHandler: TransitionHandler<T, S>,
    private val transitionParams: TransitionParams,
    private val navModel: NavModel<T, S>
) {

    @Composable
    inline fun <reified V : T> ParentNode<T>.children(
        noinline block: @Composable ChildTransitionScope<S>.(
            child: ChildRenderer,
            transitionDescriptor: TransitionDescriptor<T, S>
        ) -> Unit,
    ) {
        children(V::class, block)
    }

    @Composable
    inline fun <reified V : T> ParentNode<T>.children(
        noinline block: @Composable ChildTransitionScope<S>.(child: ChildRenderer) -> Unit,
    ) {
        children(V::class, block)
    }

    @Composable
    fun ParentNode<T>.children(
        clazz: KClass<out T>,
        block: @Composable ChildTransitionScope<S>.(child: ChildRenderer) -> Unit,
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
        block: @Composable ChildTransitionScope<S>.(
            child: ChildRenderer,
            transitionDescriptor: TransitionDescriptor<T, S>
        ) -> Unit,
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
        block: @Composable (
            transitionScope: ChildTransitionScope<S>,
            child: ChildRenderer,
            transitionDescriptor: TransitionDescriptor<T, S>
        ) -> Unit
    ) {
        val visibleElementsFlow = remember {
            this@ChildrenTransitionScope
                .navModel
                .screenState
                .map { list ->
                    list
                        .onScreen
                        .filter { clazz.isInstance(it.key.routing) }
                }
        }
        val children by visibleElementsFlow.collectAsState(emptyList())

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
