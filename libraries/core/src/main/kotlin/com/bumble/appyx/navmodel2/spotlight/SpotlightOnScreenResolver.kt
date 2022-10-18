package com.bumble.appyx.navmodel2.spotlight

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.INACTIVE_BEFORE

object SpotlightOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            INACTIVE_BEFORE,
            INACTIVE_AFTER -> false
            ACTIVE -> true
        }
}
