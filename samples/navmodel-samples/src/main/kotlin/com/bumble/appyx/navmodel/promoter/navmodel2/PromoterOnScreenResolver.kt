package com.bumble.appyx.navmodel.promoter.navmodel2

import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter.State
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver

internal object PromoterOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            State.DESTROYED -> false
            else -> true
        }
}
