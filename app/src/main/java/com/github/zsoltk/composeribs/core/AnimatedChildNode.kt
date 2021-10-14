package com.github.zsoltk.composeribs.core

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.JumpToEndTransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

@Composable
fun <Routing, State> AnimatedChildNode(
    routingSource: RoutingSource<Routing, State>,
    routingElement: RoutingElement<Routing, State>,
    childEntry: Node.ChildEntry<Routing>,
    transitionHandler: TransitionHandler<State> = JumpToEndTransitionHandler(),
    decorator: @Composable ChildTransitionScope<State>.(transitionModifier: Modifier, child: @Composable () -> Unit) -> Unit = { modifier, child ->
        Box(modifier = modifier) {
            child()
        }
    }
) {
    key(childEntry.key) {
        val transitionScope =
            transitionHandler.handle(
                fromState = routingElement.fromState,
                toState = routingElement.targetState,
                onTransitionFinished = {
                    routingSource.onTransitionFinished(childEntry.key)
                })
        transitionScope.decorator(
            transitionModifier = transitionScope.transitionModifier,
            child = { childEntry.node.Compose() },
        )
    }
}

@Composable
fun <R, S> RoutingSource<R, S>?.childrenAsState(): State<List<RoutingElement<R, S>>> =
    if (this != null) {
        all.collectAsState()
    } else {
        remember { mutableStateOf(emptyList()) }
    }

// TODO FIX ME NULL POINTER MIGRATE TO FLOW
@Composable
fun <R, S> RoutingSource<R, S>?.visibleChildAsState(): State<RoutingElement<R, S>?> =
    if (this != null) {
        all
            .map { it.findLast { it.onScreen } }
            .collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

// TODO FIX ME NULL POINTER MIGRATE TO FLOW
@Composable
fun <R, S> RoutingSource<R, S>?.visibleChildAsState(routingClazz: KClass<*>): State<RoutingElement<R, S>?> =
    if (this != null) {
        all
            .map { it.findLast { routingClazz.isInstance(it.key.routing) && it.onScreen } }
            .collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }
