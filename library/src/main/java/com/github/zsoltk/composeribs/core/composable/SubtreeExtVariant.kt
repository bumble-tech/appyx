package com.github.zsoltk.composeribs.core.composable

import android.os.Parcelable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionParams
import kotlinx.coroutines.flow.map


@Composable
inline fun <reified V : T, reified T : Parcelable, reified S : Parcelable> ParentNode<T>.SubtreeVariant(
    routingSource: RoutingSource<T, S>,
    transitionHandler: TransitionHandler<T, S>,
    crossinline block: @Composable (child: @Composable () -> Unit) -> Unit,
) {
    BoxWithConstraints {
        val onScreen by routingSource
            .onScreen
            .map {
                it
                    .filter { it.key.routing is V }
                    .map { it to childOrCreate(it.key) }
            }
            .collectAsState(emptyList())

        onScreen.forEach { (routingElement, childEntry) ->
            key(childEntry.key) {
                val transitionScope =
                    transitionHandler.handle(
                        descriptor = TransitionDescriptor(
                            params = TransitionParams(
                                bounds = TransitionBounds(
                                    width = maxWidth,
                                    height = maxHeight
                                )
                            ),
                            operation = routingElement.operation,
                            element = routingElement.key.routing,
                            fromState = routingElement.fromState,
                            toState = routingElement.targetState
                        ),
                        onTransitionFinished = {
                            routingSource.onTransitionFinished(childEntry.key)
                        }
                    )

                block(
                    child = {
                        CompositionLocalProvider(LocalTransitionModifier provides transitionScope.transitionModifier) {
                            childEntry.node.Compose()
                        }
                    }
                )
            }
        }
    }
}
