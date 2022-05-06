package com.bumble.appyx.v2.core.routing.source.tiles

import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.source.tiles.backPressHandler.DeselectAllTiles

class Tiles<T : Any>(
    initialRoutings: List<T>,
    backPressHandler: BackPressHandlerStrategy<T, TransitionState> = DeselectAllTiles()
) : BaseRoutingSource<T, Tiles.TransitionState>(
    backPressHandler = backPressHandler,
    screenResolver = TilesOnScreenResolver,
    finalState = null,
) {

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    override val initialElements = initialRoutings.map {
        TilesElement(
            key = RoutingKey(it),
            fromState = TransitionState.CREATED,
            targetState = TransitionState.STANDARD,
            operation = Operation.Noop()
        )
    }
}
