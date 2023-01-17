package com.bumble.appyx.transitionmodel.promoter

import com.bumble.appyx.transitionmodel.promoter.Promoter.State
import com.bumble.appyx.interactions.core.navigation.onscreen.OnScreenStateResolver

internal object PromoterOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            State.DESTROYED -> false
            else -> true
        }
}
