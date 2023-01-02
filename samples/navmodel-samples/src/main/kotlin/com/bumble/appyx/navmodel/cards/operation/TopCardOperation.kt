package com.bumble.appyx.navmodel.cards.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.cards.Cards
import com.bumble.appyx.navmodel.cards.CardsElements

abstract class TopCardOperation<T : Parcelable>(
    private val newTargetState: Cards.State
) : CardsOperation<T> {

    override fun isApplicable(elements: CardsElements<T>): Boolean =
        elements.any { it.targetState in Cards.TOP_STATES }

    override fun invoke(elements: CardsElements<T>): CardsElements<T> {
        val targetIndex = elements.indexOfFirst { it.targetState in Cards.TOP_STATES }
        return elements.transitionToIndexed(newTargetState) { i, _ ->
            i == targetIndex
        }
    }
}
