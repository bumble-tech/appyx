package com.github.zsoltk.composeribs.core.routing.source.backstack

import com.github.zsoltk.composeribs.core.routing.OnScreenResolver
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState
import kotlinx.parcelize.Parcelize

@Parcelize
object BackStackOnScreenResolver : OnScreenResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.CREATED,
            TransitionState.ON_SCREEN -> true
            TransitionState.STASHED_IN_BACK_STACK,
            TransitionState.DESTROYED -> false
        }
}
