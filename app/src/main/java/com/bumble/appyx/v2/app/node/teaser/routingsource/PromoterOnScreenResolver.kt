package com.bumble.appyx.v2.app.node.teaser.routingsource

import com.bumble.appyx.v2.app.node.teaser.routingsource.Promoter.TransitionState
import com.bumble.appyx.v2.core.routing.OnScreenStateResolver

internal object PromoterOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.DESTROYED -> false
            else -> true
        }
}
