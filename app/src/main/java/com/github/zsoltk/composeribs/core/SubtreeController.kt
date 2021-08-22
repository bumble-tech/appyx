package com.github.zsoltk.composeribs.core

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
        key: RoutingKey<T>,
        onRemovedFromScreen: () -> Unit,
        onDestroyed: () -> Unit,
    ): Modifier {
        val element =
            routingSource.elements.firstOrNull { it.key == key }
                ?: routingSource.pendingRemoval.firstOrNull { it.key == key }

        return element?.let {
            transitionHandler.handle(
                transitionState = element.targetState,
                onRemovedFromScreen = onRemovedFromScreen,
                onDestroyed = onDestroyed
            )
        } ?: Modifier.background(color = Color.Magenta ) // TODO return Modifier and log error instead
    }
}
