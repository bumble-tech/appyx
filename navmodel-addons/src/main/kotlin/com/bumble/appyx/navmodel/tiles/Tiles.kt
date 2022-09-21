package com.bumble.appyx.navmodel.tiles

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.navmodel.tiles.backPressHandler.DeselectAllTiles
import com.bumble.appyx.navmodel.tiles.Tiles.TransitionState
import com.bumble.appyx.navmodel.tiles.Tiles.TransitionState.CREATED
import com.bumble.appyx.navmodel.tiles.Tiles.TransitionState.STANDARD

class Tiles<T : Any>(
    initialItems: List<T>,
    backPressHandler: BackPressHandlerStrategy<T, TransitionState> = DeselectAllTiles()
) : BaseNavModel<T, TransitionState>(
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
            key = NavKey(it),
            fromState = CREATED,
            targetState = STANDARD,
            operation = Noop()
        )
    }
}
