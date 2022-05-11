package com.bumble.appyx.v2.core.routing.source.tiles

import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.source.tiles.backPressHandler.DeselectAllTiles

class Tiles<T : Any>(
    initialElements: List<T>,
    backPressHandler: BackPressHandlerStrategy<T, TransitionState> = DeselectAllTiles()
) : BaseRoutingSource<T, Tiles.TransitionState>(
    backPressHandler = backPressHandler,
    screenResolver = TilesOnScreenResolver
) {

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    override val initialState: RoutingElements<T, TransitionState> =
        initialElements.map {
            TilesElement(
                key = RoutingKey(it),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.STANDARD,
                operation = Operation.Noop()
            )
        }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        updateState { list ->
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
