package com.bumble.appyx.transitionmodel.promoter

import com.bumble.appyx.interactions.core.ui.onscreen.OnScreenStateResolver
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State

internal object PromoterOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            State.DESTROYED -> false
            else -> true
        }
}
