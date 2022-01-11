package com.github.zsoltk.composeribs.core.composable

import androidx.compose.animation.core.Transition
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
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
        child: @Composable () -> Unit,
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
            transitionDescriptor = descriptor,
            child = {
                CompositionLocalProvider(LocalTransitionModifier provides transitionScope.transitionModifier) {
                    childEntry.node.Compose()
                }
            },
        )
    }
}

@Composable
fun <Routing : Any, State> ParentNode<Routing>.Child(
    routingElement: RoutingElement<Routing, out State>,
    transitionHandler: TransitionHandler<Routing, State> = JumpToEndTransitionHandler(),
    decorator: @Composable ChildTransitionScope<State>.(
        child: @Composable () -> Unit,
        transitionDescriptor: TransitionDescriptor<Routing, State>
    ) -> Unit = { child, _ -> child() }
) {
    BoxWithConstraints {
        Child(
            routingElement = routingElement,
            saveableStateHolder = rememberSaveableStateHolder(),
            transitionParams = remember(maxWidth, maxHeight) {
                TransitionParams(
                    bounds = TransitionBounds(
                        width = maxWidth,
                        height = maxHeight
                    )
                )
            },
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
