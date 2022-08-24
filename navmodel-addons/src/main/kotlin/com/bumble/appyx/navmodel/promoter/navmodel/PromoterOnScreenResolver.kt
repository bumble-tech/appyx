package com.bumble.appyx.navmodel.promoter.navmodel

import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.TransitionState
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver

internal object PromoterOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.DESTROYED -> false
            else -> true
        }
}
