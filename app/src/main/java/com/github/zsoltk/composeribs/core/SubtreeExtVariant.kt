package com.github.zsoltk.composeribs.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler


@Composable
inline fun <reified V : T, reified T, reified S> Node<T>.SubtreeVariant(
    routingSource: RoutingSource<T, S>,
    transitionHandler: TransitionHandler<S>,
    block: @Composable (transitionModifier: Modifier, child: @Composable () -> Unit) -> Unit,
) {
    val onScreen =
//            remember {
        routingSource.onScreen
            .filter { it.key.routing is V || it.key.routing!!::class.java.isAssignableFrom(T::class.java) }
            .map { it to childOrCreate(it.key) }
//        }

    onScreen.forEach { (routingElement, childEntry) ->
        key(childEntry.key) {
            val transitionModifier =
                transitionHandler.handle(
                    fromState = routingElement.fromState,
                    toState = routingElement.targetState,
                    onTransitionFinished = {
                        routingSource.onTransitionFinished(childEntry.key)
                    })

            block(
                transitionModifier = transitionModifier,
                child = { childEntry.node.Compose() },
            )
        }
    }
}
