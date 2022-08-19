package com.bumble.appyx.navmodel.backstack

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState

object BackStackOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.CREATED,
            TransitionState.STASHED_IN_BACK_STACK,
            TransitionState.DESTROYED -> false
            TransitionState.ACTIVE -> true
        }
}
