package com.bumble.appyx.components.experimental.cards.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.experimental.cards.Cards
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.components.experimental.cards.CardsModel
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.InvisibleCard.VotedCard.VOTED_CARD_STATE.LIKED
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.VisibleCard.BottomCard
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.VisibleCard.TopCard
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.VisibleCard.TopCard.TOP_CARD_STATE.STANDARD

@Parcelize
class VoteLike<InteractionTarget>(
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
): TopCardOperation<InteractionTarget>() {

    override fun createTargetState(fromState: CardsModel.State<InteractionTarget>): CardsModel.State<InteractionTarget> {
        val votedCards = fromState.votedCards
        return CardsModel.State(
            votedCards = votedCards + CardsModel.State.Card.InvisibleCard.VotedCard(
                fromState.visibleCards.first().element, LIKED
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
            result.add(TopCard(fromState.visibleCards[1].element, STANDARD))
            if (fromState.queued.isNotEmpty()) {
                result.add(BottomCard(fromState.queued.first().element))
            }
            result
        }
    }
}

fun <InteractionTarget : Any> Cards<InteractionTarget>.like(
    mode: Operation.Mode = Operation.Mode.KEYFRAME,
    animationSpec: AnimationSpec<Float>? = null
) {
    operation(operation = VoteLike(mode), animationSpec = animationSpec)
}
