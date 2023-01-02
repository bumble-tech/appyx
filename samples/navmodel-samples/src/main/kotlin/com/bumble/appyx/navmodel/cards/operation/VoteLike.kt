package com.bumble.appyx.navmodel.cards.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.cards.Cards
import kotlinx.parcelize.Parcelize

@Parcelize
class VoteLike<T : Parcelable> internal constructor() : TopCardOperation<T>(
    newTargetState = Cards.State.VoteLike
)

fun <T : Parcelable> Cards<T>.voteLike() {
    accept(VoteLike())
    promoteAll()
}
