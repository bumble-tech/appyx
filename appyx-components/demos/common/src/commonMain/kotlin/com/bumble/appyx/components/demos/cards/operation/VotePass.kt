package com.bumble.appyx.components.demos.cards.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.components.demos.cards.CardsModel
import com.bumble.appyx.components.demos.cards.CardsModel.State.Card.InvisibleCard.VotedCard.VOTED_CARD_STATE.PASSED

@Parcelize
class VotePass<InteractionTarget>(
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : TopCardOperation<InteractionTarget>() {

    override fun createTargetState(fromState: CardsModel.State<InteractionTarget>): CardsModel.State<InteractionTarget> {
        val votedCards = fromState.votedCards
        return CardsModel.State(
            votedCards = votedCards + CardsModel.State.Card.InvisibleCard.VotedCard(
                fromState.visibleCards.first().element,
                PASSED
            ),
            visibleCards = resolveVisibleCards(fromState),
            queued = fromState.queued.drop(1)
        )
    }

    private fun resolveVisibleCards(fromState: CardsModel.State<InteractionTarget>): List<CardsModel.State.Card.VisibleCard<InteractionTarget>> {
        return if (fromState.visibleCards.size < 2) {
            emptyList()
        } else {
            val result = mutableListOf<CardsModel.State.Card.VisibleCard<InteractionTarget>>()
            result.add(
                CardsModel.State.Card.VisibleCard.TopCard(
                    fromState.visibleCards[1].element,
                    CardsModel.State.Card.VisibleCard.TopCard.TOP_CARD_STATE.STANDARD
                )
            )
            if (fromState.queued.isNotEmpty()) {
                result.add(CardsModel.State.Card.VisibleCard.BottomCard(fromState.queued.first().element))
            }
            result
        }
    }
}
