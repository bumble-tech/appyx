package com.bumble.appyx.navmodel2.cards.operation

import com.bumble.appyx.core.navigation2.NavTransition
import com.bumble.appyx.navmodel2.cards.Cards
import com.bumble.appyx.navmodel2.cards.CardsElements

abstract class TopCardOperation<NavTarget : Any>(
    private val newTargetState: Cards.State
) : CardsOperation<NavTarget> {

    override fun isApplicable(elements: CardsElements<NavTarget>): Boolean =
        elements.any { it.state in Cards.TOP_STATES }

    override fun invoke(elements: CardsElements<NavTarget>): NavTransition<NavTarget, Cards.State> {
        val targetIndex = elements.indexOfFirst { it.state in Cards.TOP_STATES }

        return NavTransition(
            fromState = elements,
            targetState = elements.mapIndexed { index, element ->
                if (index == targetIndex) {
                    element.transitionTo(newTargetState = newTargetState, this)
                } else {
                    element.transitionTo(newTargetState = element.state.next(), this)
                }
            }
        )
    }
}
