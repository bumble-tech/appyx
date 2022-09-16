package com.bumble.appyx.navmodel.spotlightadvanced

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState.Active
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState.Carousel
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState.InactiveAfter
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState.InactiveBefore

object SpotlightAdvancedOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            is InactiveBefore,
            is InactiveAfter -> false
            is Active,
            is Carousel -> true
        }
}
