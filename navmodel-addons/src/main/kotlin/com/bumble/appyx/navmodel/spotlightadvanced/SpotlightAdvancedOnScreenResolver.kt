package com.bumble.appyx.navmodel.spotlightadvanced

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Active
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Carousel
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.InactiveAfter
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.InactiveBefore

object SpotlightAdvancedOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            is InactiveBefore,
            is InactiveAfter -> false
            is Active,
            is Carousel -> true
        }
}
