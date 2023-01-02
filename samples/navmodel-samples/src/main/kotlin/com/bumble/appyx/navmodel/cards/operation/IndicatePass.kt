package com.bumble.appyx.navmodel.cards.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.cards.Cards
import com.bumble.appyx.navmodel.cards.Cards.State.IndicatePass
import kotlinx.parcelize.Parcelize

@Parcelize
class IndicatePass<T : Parcelable> : TopCardOperation<T>(
    newTargetState = IndicatePass
)

fun <T : Parcelable> Cards<T>.indicatePass() {
    accept(IndicatePass())
}
