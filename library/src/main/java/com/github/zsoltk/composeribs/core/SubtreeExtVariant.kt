package com.github.zsoltk.composeribs.core

import android.os.Parcelable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionParams
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt


@Composable
inline fun <reified V : T, reified T : Parcelable, reified S : Parcelable> Node<T>.SubtreeVariant(
    routingSource: RoutingSource<T, S>,
    transitionHandler: TransitionHandler<S>,
    crossinline block: @Composable (transitionModifier: Modifier, child: @Composable () -> Unit) -> Unit,
) {
    BoxWithConstraints {
        val onScreen by routingSource
            .onScreen
            .map {
                it
                    .filter { it.key.routing is V }
                    .map { it to childOrCreate(it.key) }
            }
            .collectAsState(initial = emptyList())

        onScreen.forEach { (routingElement, childEntry) ->
            key(childEntry.key) {
                val transitionScope =
                    transitionHandler.handle(
                        fromState = routingElement.fromState,
                        toState = routingElement.targetState,
                        onTransitionFinished = {
                            routingSource.onTransitionFinished(childEntry.key)
                        },
                        transitionParams = TransitionParams(
                            bounds = TransitionBounds(
                                width = maxWidth,
                                height = maxHeight
                            )
                        )
                    )

                block(
                    transitionModifier = transitionScope.transitionModifier,
                    child = { childEntry.node.Compose() },
                )
            }
        }
    }
}
