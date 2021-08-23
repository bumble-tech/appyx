package com.github.zsoltk.composeribs.core.routing

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class SubtreeController<T, S>(
    val routingSource: RoutingSource<T, S>,
    val resolver: Resolver<T>,
    val transitionHandler: TransitionHandler<S>
) {

    @Composable
    fun whatever(
        key: RoutingKey<T>
    ): Modifier {
        val element =
            routingSource.elements.firstOrNull { it.key == key }
                ?: routingSource.pendingRemoval.firstOrNull { it.key == key }

        return element?.let {
            transitionHandler.handle(
                fromState = element.fromState,
                toState = element.targetState,
                onTransitionFinished = {
                    routingSource.onTransitionFinished(key, it)
                }
            )
        } ?: Modifier.background(color = Color.Magenta ) // TODO return Modifier and log error instead
    }
}
