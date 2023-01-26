package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.transitionmodel.cards.CardsModel
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.InvisibleCard.VotedCard.VOTED_CARD_STATE.LIKED
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.VisibleCard.BottomCard
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.VisibleCard.TopCard
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.VisibleCard.TopCard.TOP_CARD_STATE.STANDARD

@Parcelize
class VoteLike<NavTarget> : TopCardOperation<NavTarget>() {

    override fun createTargetState(fromState: CardsModel.State<NavTarget>): CardsModel.State<NavTarget> {
        val votedCards = fromState.votedCards
        return CardsModel.State(
            votedCards = votedCards + CardsModel.State.Card.InvisibleCard.VotedCard(
                fromState.visibleCards.first().navElement, LIKED
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
            result.add(TopCard(fromState.visibleCards[1].navElement, STANDARD))
            if (fromState.queued.isNotEmpty()) {
                result.add(BottomCard(fromState.queued.first().navElement))
            }
            result
        }
    }
}
