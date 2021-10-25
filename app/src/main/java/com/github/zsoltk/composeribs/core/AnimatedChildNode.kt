package com.github.zsoltk.composeribs.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.github.zsoltk.composeribs.core.children.ChildEntry
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
    childEntry: ChildEntry.Eager<Routing>,
    transitionHandler: TransitionHandler<State> = JumpToEndTransitionHandler(),
    decorator: @Composable ChildTransitionScope<State>.(transitionModifier: Modifier, child: @Composable () -> Unit) -> Unit = { modifier, child ->
        Box(modifier = modifier) {
            child()
        }
    }
) {
    key(childEntry.key) {
        var size by remember { mutableStateOf(IntSize.Zero) }
        Box(
            Modifier
                .fillMaxSize()
                .onSizeChanged {
                    size = it
                }
        ) {
            val transitionScope =
                transitionHandler.handle(
                    fromState = routingElement.fromState,
                    toState = routingElement.targetState,
                    onTransitionFinished = {
                        routingSource.onTransitionFinished(childEntry.key)
                    },
                    params = TransitionParams(size, true)
                )
            transitionScope.decorator(
                transitionModifier = transitionScope.transitionModifier,
                child = { childEntry.node.Compose() },
            )
        }

    }
}

@Composable
fun <R, S> RoutingSource<R, S>?.childrenAsState(): State<List<RoutingElement<R, S>>> =
    if (this != null) {
        all.collectAsState()
    } else {
        remember { mutableStateOf(emptyList()) }
    }

@Composable
fun <R, S> RoutingSource<R, S>?.visibleChildAsState(): State<RoutingElement<R, S>?> =
    if (this != null) {
        all
            .map { it.findLast { isOnScreen(it.key) } }
            .collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

@Composable
fun <R, S> RoutingSource<R, S>?.visibleChildAsState(routingClazz: KClass<*>): State<RoutingElement<R, S>?> =
    if (this != null) {
        all
            .map { it.findLast { routingClazz.isInstance(it.key.routing) && isOnScreen(it.key) } }
            .collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }
