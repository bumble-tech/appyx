package com.bumble.appyx.navmodel.tiles

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.navmodel.tiles.Tiles.State

internal object TilesOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            State.CREATED,
            State.STANDARD,
            State.SELECTED -> true
            State.DESTROYED -> false
        }
}
