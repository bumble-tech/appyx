package com.bumble.appyx.navmodel.cards.operation

import com.bumble.appyx.navmodel.cards.Cards
import com.bumble.appyx.navmodel.cards.Cards.Companion.FINAL_STATES
import com.bumble.appyx.navmodel.cards.CardsElements
import kotlinx.parcelize.Parcelize

@Parcelize
class VotePass<T : Any> internal constructor() : TopCardOperation<T>(
    newTargetState = Cards.State.VotePass
)

fun <T : Any> Cards<T>.votePass() {
    accept(VotePass())
    promoteAll()
}
