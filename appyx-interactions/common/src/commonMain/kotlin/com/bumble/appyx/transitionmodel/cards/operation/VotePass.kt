package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.cards.CardsModel
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.InvisibleCard.VotedCard.VOTED_CARD_STATE.PASSED

@Parcelize
class VotePass<NavTarget>(
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : TopCardOperation<NavTarget>() {

    override fun createTargetState(fromState: CardsModel.State<NavTarget>): CardsModel.State<NavTarget> {
        val votedCards = fromState.votedCards
        return CardsModel.State(
            votedCards = votedCards + CardsModel.State.Card.InvisibleCard.VotedCard(
                fromState.visibleCards.first().navElement,
                PASSED
            ),
            visibleCards = resolveVisibleCards(fromState),
            queued = fromState.queued.drop(1)
        )
    }

    private fun resolveVisibleCards(fromState: CardsModel.State<NavTarget>): List<CardsModel.State.Card.VisibleCard<NavTarget>> {
        return if (fromState.visibleCards.size < 2) {
            emptyList()
        } else {
            val result = mutableListOf<CardsModel.State.Card.VisibleCard<NavTarget>>()
            result.add(
                CardsModel.State.Card.VisibleCard.TopCard(
                    fromState.visibleCards[1].navElement,
                    CardsModel.State.Card.VisibleCard.TopCard.TOP_CARD_STATE.STANDARD
                )
            )
            if (fromState.queued.isNotEmpty()) {
                result.add(CardsModel.State.Card.VisibleCard.BottomCard(fromState.queued.first().navElement))
            }
            result
        }
    }
}
