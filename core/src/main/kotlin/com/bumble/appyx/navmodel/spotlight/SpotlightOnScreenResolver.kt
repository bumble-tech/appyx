package com.bumble.appyx.navmodel.spotlight

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.INACTIVE_AFTER
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.INACTIVE_BEFORE

object SpotlightOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            INACTIVE_BEFORE,
            INACTIVE_AFTER -> false
            ACTIVE -> true
        }
}
