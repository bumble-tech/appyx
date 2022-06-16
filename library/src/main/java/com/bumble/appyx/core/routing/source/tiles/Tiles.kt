package com.bumble.appyx.core.routing.source.tiles

import com.bumble.appyx.core.routing.BaseRoutingSource
import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.routing.source.tiles.backPressHandler.DeselectAllTiles

class Tiles<T : Any>(
    initialItems: List<T>,
    backPressHandler: BackPressHandlerStrategy<T, TransitionState> = DeselectAllTiles()
) : BaseRoutingSource<T, Tiles.TransitionState>(
    backPressHandler = backPressHandler,
    screenResolver = TilesOnScreenResolver,
    finalState = null,
    savedStateMap = null,
) {

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    override val initialElements = initialItems.map {
        TilesElement(
            key = RoutingKey(it),
            fromState = TransitionState.CREATED,
            targetState = TransitionState.STANDARD,
            operation = Operation.Noop()
        )
    }
}
