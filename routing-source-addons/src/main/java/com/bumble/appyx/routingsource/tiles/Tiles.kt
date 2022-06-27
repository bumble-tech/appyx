package com.bumble.appyx.routingsource.tiles

import com.bumble.appyx.core.routing.BaseRoutingSource
import com.bumble.appyx.core.routing.Operation.Noop
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.routingsource.tiles.backPressHandler.DeselectAllTiles
import com.bumble.appyx.routingsource.tiles.Tiles.TransitionState
import com.bumble.appyx.routingsource.tiles.Tiles.TransitionState.CREATED
import com.bumble.appyx.routingsource.tiles.Tiles.TransitionState.STANDARD

class Tiles<T : Any>(
    initialItems: List<T>,
    backPressHandler: BackPressHandlerStrategy<T, TransitionState> = DeselectAllTiles()
) : BaseRoutingSource<T, TransitionState>(
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
            fromState = CREATED,
            targetState = STANDARD,
            operation = Noop()
        )
    }
}
