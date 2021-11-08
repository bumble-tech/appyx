package com.github.zsoltk.composeribs.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.children.ChildEntry
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.JumpToEndTransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionParams
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

@Composable
fun <Routing, State> AnimatedChildNode(
    routingSource: RoutingSource<Routing, State>,
    routingElement: RoutingElement<Routing, State>,
    operation: Operation<Routing, State>,
    childEntry: ChildEntry.Eager<Routing>,
    transitionHandler: TransitionHandler<Routing, State> = JumpToEndTransitionHandler(),
    decorator: @Composable ChildTransitionScope<State>.(transitionModifier: Modifier, child: @Composable () -> Unit) -> Unit = { modifier, child ->
        Box(modifier = modifier) {
            child()
        }
    }
) {
    key(childEntry.key) {
        BoxWithConstraints {
            val transitionScope =
                transitionHandler.handle(
                    descriptor = TransitionDescriptor(
                        transitionParams = TransitionParams(
                            bounds = TransitionBounds(
                                width = maxWidth,
                                height = maxHeight
                            )
                        ),
                        operation = operation,
                        element = routingElement.key.routing,
                        fromState = routingElement.fromState,
                        toState = routingElement.targetState
                    ),
                    onTransitionFinished = {
                        routingSource.onTransitionFinished(childEntry.key)
                    }
                )
            transitionScope.decorator(
                transitionModifier = transitionScope.transitionModifier,
                child = { childEntry.node.Compose() },
            )
        }

    }
}

@Composable
fun <R, S> RoutingSource<R, S>?.childrenAsState(): State<RoutingElements<R, S>> =
    if (this != null) {
        all.map { it.elements }.collectAsState(all.value.elements)
    } else {
        remember { mutableStateOf(emptyList()) }
    }

@Composable
fun <R, S> RoutingSource<R, S>?.visibleChildAsState(): State<RoutingElement<R, S>?> =
    if (this != null) {
        all
            .map { it.elements }
            .map { it.findLast { isOnScreen(it.key) } }
            .collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

@Composable
fun <R, S> RoutingSource<R, S>?.visibleChildAsState(routingClazz: KClass<*>): State<RoutingElement<R, S>?> =
    if (this != null) {
        all
            .map { it.elements }
            .map { it.findLast { routingClazz.isInstance(it.key.routing) && isOnScreen(it.key) } }
            .collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }
