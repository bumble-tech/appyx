package com.bumble.appyx.navmodel.tiles

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.navmodel.tiles.Tiles.State
import com.bumble.appyx.navmodel.tiles.Tiles.State.CREATED
import com.bumble.appyx.navmodel.tiles.Tiles.State.STANDARD
import com.bumble.appyx.navmodel.tiles.backPressHandler.DeselectAllTiles

class Tiles<T : Any>(
    initialItems: List<T>,
    backPressHandler: BackPressHandlerStrategy<T, State> = DeselectAllTiles()
) : BaseNavModel<T, State>(
    backPressHandler = backPressHandler,
    screenResolver = TilesOnScreenResolver,
    initialElements = initialItems.map {
        TilesElement(
            key = NavKey(it),
            fromState = CREATED,
            targetState = STANDARD,
            operation = Noop()
        )
    },
    finalState = null,
    savedStateMap = null,
) {

    enum class State {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

}
