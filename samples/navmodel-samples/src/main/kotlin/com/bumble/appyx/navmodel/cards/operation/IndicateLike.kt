package com.bumble.appyx.navmodel.cards.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.cards.Cards
import com.bumble.appyx.navmodel.cards.Cards.Companion.TOP_STATES
import com.bumble.appyx.navmodel.cards.CardsElements
import kotlinx.parcelize.Parcelize

@Parcelize
class IndicateLike<T : Parcelable> : TopCardOperation<T>(
    newTargetState = Cards.State.IndicateLike
)

fun <T : Parcelable> Cards<T>.indicateLike() {
    accept(IndicateLike())
}
