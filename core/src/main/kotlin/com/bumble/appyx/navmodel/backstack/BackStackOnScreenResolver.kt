package com.bumble.appyx.navmodel.backstack

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.navmodel.backstack.BackStack.State

object BackStackOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            State.CREATED,
            State.STASHED,
            State.DESTROYED -> false
            State.ACTIVE -> true
        }
}
