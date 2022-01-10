package com.github.zsoltk.composeribs.core.routing.source.backstack

import com.github.zsoltk.composeribs.core.routing.OnScreenStateResolver
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState

object BackStackOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.CREATED,
            TransitionState.STASHED_IN_BACK_STACK,
            TransitionState.DESTROYED -> false
            TransitionState.ACTIVE -> true
        }
}
