package com.bumble.appyx.navmodel.cards

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.navmodel.cards.Cards.State


internal object CardsOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            is State.Bottom,
            is State.Top,
            is State.IndicateLike,
            is State.IndicatePass -> true
            is State.Queued,
            is State.VoteLike,
            is State.VotePass -> false
        }
}
