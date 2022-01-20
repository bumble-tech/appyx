package com.github.zsoltk.composeribs.core.composable

import androidx.compose.animation.core.Transition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.JumpToEndTransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionParams

@Composable
fun <Routing : Any, State> ParentNode<Routing>.Child(
    routingElement: RoutingElement<Routing, out State>,
    saveableStateHolder: SaveableStateHolder,
    transitionParams: TransitionParams,
    transitionHandler: TransitionHandler<Routing, State>,
    decorator: @Composable ChildTransitionScope<State>.(
        child: ChildRenderer,
        transitionDescriptor: TransitionDescriptor<Routing, State>
    ) -> Unit
) {
    val childEntry = childOrCreate(routingElement.key)
    saveableStateHolder.SaveableStateProvider(key = routingElement.key) {
        val descriptor = routingElement.createDescriptor(transitionParams)
        val transitionScope =
            transitionHandler.handle(
                descriptor = descriptor,
                onTransitionFinished = {
                    routingSource.onTransitionFinished(
                        childEntry.key
                    )
                })

        transitionScope.decorator(
            child = ChildRendererImpl(node = childEntry.node, transitionModifier = transitionScope.transitionModifier),
            transitionDescriptor = descriptor,
        )
    }
}

interface ChildRenderer {
    @Composable
    operator fun invoke(modifier: Modifier)

    @Composable
    operator fun invoke()
}

private class ChildRendererImpl(
    private val node: Node,
    private val transitionModifier: Modifier
) : ChildRenderer {
    @Composable
    override operator fun invoke(modifier: Modifier) {
        CompositionLocalProvider(LocalTransitionModifier provides transitionModifier) {
            node.Compose()
        }
    }

    @Composable
    override operator fun invoke() {
        invoke(modifier = Modifier)
    }
}

@Composable
fun <Routing : Any, State> ParentNode<Routing>.Child(
    routingElement: RoutingElement<Routing, out State>,
    transitionHandler: TransitionHandler<Routing, State> = JumpToEndTransitionHandler(),
    decorator: @Composable ChildTransitionScope<State>.(
        child: ChildRenderer,
        transitionDescriptor: TransitionDescriptor<Routing, State>
    ) -> Unit = { child, _ -> child.invoke() }
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
    Box(modifier = Modifier
        .onSizeChanged {
            transitionBounds = it
        }
    ) {
        Child(
            routingElement = routingElement,
            saveableStateHolder = rememberSaveableStateHolder(),
            transitionParams = transitionParams,
            transitionHandler = transitionHandler,
            decorator = decorator
        )
    }
}

private fun <Routing : Any, State> RoutingElement<Routing, State>.createDescriptor(
    transitionParams: TransitionParams
) =
    TransitionDescriptor(
        params = transitionParams,
        operation = operation,
        element = key.routing,
        fromState = fromState,
        toState = targetState
    )

@Composable
fun <R, S> RoutingSource<R, S>?.childrenAsState(): State<RoutingElements<R, out S>> =
    if (this != null) {
        elements.collectAsState()
    } else {
        remember { mutableStateOf(emptyList()) }
    }

@Composable
fun <R, S> RoutingSource<R, S>?.visibleChildrenAsState(): State<RoutingElements<R, out S>> =
    if (this != null) {
        onScreen.collectAsState()
    } else {
        remember { mutableStateOf(emptyList()) }
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
