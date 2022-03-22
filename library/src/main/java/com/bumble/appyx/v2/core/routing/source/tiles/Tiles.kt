package com.bumble.appyx.v2.core.routing.source.tiles

import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.source.tiles.backPressHandler.DeselectAllTiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class Tiles<T : Any>(
    initialElements: List<T>,
    backPressHandler: BackPressHandlerStrategy<T, TransitionState, Tiles<T>> = DeselectAllTiles()
) : BaseRoutingSource<T, Tiles.TransitionState, Tiles<T>>(
    backPressHandler = backPressHandler,
    screenResolver = TilesOnScreenResolver
) {

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    override val state = MutableStateFlow(
        initialElements.map {
            TilesElement(
                key = RoutingKey(it),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.STANDARD,
                operation = Operation.Noop()
            )
        }
    )

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    if (it.targetState == TransitionState.DESTROYED) {
                        null
                    } else {
                        it.onTransitionFinished()
                    }
                } else {
                    it
                }
            }
        }
    }
}
