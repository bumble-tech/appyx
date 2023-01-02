package com.bumble.appyx.navmodel.tiles

import android.os.Parcelable
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.navmodel.tiles.backPressHandler.DeselectAllTiles
import com.bumble.appyx.navmodel.tiles.Tiles.State
import com.bumble.appyx.navmodel.tiles.Tiles.State.CREATED
import com.bumble.appyx.navmodel.tiles.Tiles.State.STANDARD
import kotlinx.parcelize.Parcelize

class Tiles<T : Parcelable>(
    initialItems: List<T>,
    backPressHandler: BackPressHandlerStrategy<T, State> = DeselectAllTiles()
) : BaseNavModel<T, State>(
    backPressHandler = backPressHandler,
    screenResolver = TilesOnScreenResolver,
    finalState = null,
    savedStateMap = null,
) {

    @Parcelize
    enum class State : Parcelable {
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
