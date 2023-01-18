package com.bumble.appyx.transitionmodel.spotlight

import com.bumble.appyx.interactions.core.ui.onscreen.OnScreenStateResolver
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ACTIVE
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.INACTIVE_AFTER
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.INACTIVE_BEFORE

object SpotlightOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            INACTIVE_BEFORE,
            INACTIVE_AFTER -> false
            ACTIVE -> true
        }
}
