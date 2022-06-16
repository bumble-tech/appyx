package com.bumble.appyx.app.node.teaser.promoter.routingsource

import com.bumble.appyx.app.node.teaser.promoter.routingsource.Promoter.TransitionState
import com.bumble.appyx.core.routing.onscreen.OnScreenStateResolver

internal object PromoterOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.DESTROYED -> false
            else -> true
        }
}
