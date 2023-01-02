package com.bumble.appyx.navmodel.cards.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.cards.Cards
import com.bumble.appyx.navmodel.cards.Cards.Companion.FINAL_STATES
import com.bumble.appyx.navmodel.cards.CardsElements
import kotlinx.parcelize.Parcelize

@Parcelize
class VotePass<T : Parcelable> internal constructor() : TopCardOperation<T>(
    newTargetState = Cards.State.VotePass
)

fun <T : Parcelable> Cards<T>.votePass() {
    accept(VotePass())
    promoteAll()
}
