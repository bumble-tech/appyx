package com.bumble.appyx.v2.core.routing.source.tiles

import com.bumble.appyx.v2.core.routing.OnScreenStateResolver
import com.bumble.appyx.v2.core.routing.source.tiles.Tiles.TransitionState

internal object TilesOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.CREATED,
            TransitionState.STANDARD,
            TransitionState.SELECTED -> true
            TransitionState.DESTROYED -> false
        }
}
