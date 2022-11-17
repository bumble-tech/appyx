package com.bumble.appyx.navmodel.cards.operation

import com.bumble.appyx.navmodel.cards.Cards
import kotlinx.parcelize.Parcelize

@Parcelize
class VoteLike<T : Any> internal constructor() : TopCardOperation<T>(
    newTargetState = Cards.State.VoteLike
)

fun <T : Any> Cards<T>.voteLike() {
    accept(VoteLike())
    promoteAll()
}
