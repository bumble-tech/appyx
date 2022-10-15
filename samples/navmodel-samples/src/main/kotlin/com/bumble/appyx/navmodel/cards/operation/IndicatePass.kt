package com.bumble.appyx.navmodel.cards.operation

import com.bumble.appyx.navmodel.cards.Cards
import com.bumble.appyx.navmodel.cards.Cards.State.IndicatePass
import kotlinx.parcelize.Parcelize

@Parcelize
class IndicatePass<T : Any> : TopCardOperation<T>(
    newTargetState = IndicatePass
)

fun <T : Any> Cards<T>.indicatePass() {
    accept(IndicatePass())
}
