package com.bumble.appyx.transitionmodel.spotlight

import com.bumble.appyx.interactions.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State.INACTIVE_BEFORE

object SpotlightOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            INACTIVE_BEFORE,
            INACTIVE_AFTER -> false
            ACTIVE -> true
        }
}
