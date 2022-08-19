package com.bumble.appyx.navmodel.tiles

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.navmodel.tiles.Tiles.TransitionState

internal object TilesOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.CREATED,
            TransitionState.STANDARD,
            TransitionState.SELECTED -> true
            TransitionState.DESTROYED -> false
        }
}
