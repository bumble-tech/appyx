package com.bumble.appyx.v2.core.routing.source.backstack

import com.bumble.appyx.v2.core.routing.onscreen.OnScreenStateResolver
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState

object BackStackOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.CREATED,
            TransitionState.STASHED_IN_BACK_STACK,
            TransitionState.DESTROYED -> false
            TransitionState.ACTIVE -> true
        }
}
