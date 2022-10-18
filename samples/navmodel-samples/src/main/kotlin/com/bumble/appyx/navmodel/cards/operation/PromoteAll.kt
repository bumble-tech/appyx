package com.bumble.appyx.navmodel.cards.operation

import com.bumble.appyx.navmodel.cards.Cards
import com.bumble.appyx.navmodel.cards.CardsElements
import kotlinx.parcelize.Parcelize

@Parcelize
internal class PromoteAll<T : Any> : CardsOperation<T> {

    override fun isApplicable(elements: CardsElements<T>): Boolean =
        true

    override fun invoke(elements: CardsElements<T>, ): CardsElements<T> =
        elements.transitionTo { it.targetState.next() }
}

internal fun <T : Any> Cards<T>.promoteAll() {
    accept(PromoteAll())
}
