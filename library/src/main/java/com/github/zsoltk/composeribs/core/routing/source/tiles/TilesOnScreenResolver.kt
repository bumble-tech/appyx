package com.github.zsoltk.composeribs.core.routing.source.tiles

import com.github.zsoltk.composeribs.core.routing.OnScreenStateResolver
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles.TransitionState

internal object TilesOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.CREATED,
            TransitionState.STANDARD,
            TransitionState.SELECTED -> true
            TransitionState.DESTROYED -> false
        }
}