package com.bumble.appyx.transitionmodel.cards

import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.InvisibleCard.Queued
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.VisibleCard.BottomCard
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.VisibleCard.TopCard

class CardsModel<InteractionTarget : Any>(
    initialItems: List<InteractionTarget> = listOf(),
) : BaseTransitionModel<InteractionTarget, CardsModel.State<InteractionTarget>>() {

    data class State<InteractionTarget>(
        val votedCards: List<Card.InvisibleCard<InteractionTarget>> = emptyList(),
        val visibleCards: List<Card.VisibleCard<InteractionTarget>> = emptyList(),
        val queued: List<Card.InvisibleCard<InteractionTarget>> = emptyList()
    ) {
        sealed class Card<InteractionTarget> {
            abstract val navElement: NavElement<InteractionTarget>

            sealed class VisibleCard<InteractionTarget> : Card<InteractionTarget>() {
                data class TopCard<InteractionTarget>(
                    override val navElement: NavElement<InteractionTarget>,
                    val topCardState: TOP_CARD_STATE
                ) : VisibleCard<InteractionTarget>() {
                    enum class TOP_CARD_STATE {
                        STANDARD, INDICATE_LIKE, INDICATE_PASS
                    }
                }

                data class BottomCard<InteractionTarget>(
                    override val navElement: NavElement<InteractionTarget>
                ) : VisibleCard<InteractionTarget>()
            }

            sealed class InvisibleCard<InteractionTarget> : Card<InteractionTarget>() {

                data class Queued<InteractionTarget>(
                    override val navElement: NavElement<InteractionTarget>
                ) : InvisibleCard<InteractionTarget>()

                data class VotedCard<InteractionTarget>(
                    override val navElement: NavElement<InteractionTarget>,
                    val votedCardState: VOTED_CARD_STATE
                ) : InvisibleCard<InteractionTarget>() {
                    enum class VOTED_CARD_STATE {
                        LIKED, PASSED
                    }
                }
            }
        }
    }

    fun getInitialState(initialItems: List<InteractionTarget>): State<InteractionTarget> {
        val visibleCards: MutableList<State.Card.VisibleCard<InteractionTarget>> = mutableListOf()
        val queuedCards: MutableList<State.Card.InvisibleCard<InteractionTarget>> = mutableListOf()
        initialItems.mapIndexed { index, item ->
            when (index) {
                0 -> {
                    visibleCards.add(
                        TopCard(item.asElement(), TopCard.TOP_CARD_STATE.STANDARD)
                    )
                }
                1 -> {
                    visibleCards.add(BottomCard(item.asElement()))
                }
                else -> queuedCards.add(Queued(item.asElement()))
            }

        }
        return State(visibleCards = visibleCards, queued = queuedCards)
    }


    override val initialState: State<InteractionTarget> = getInitialState(initialItems)

    override fun State<InteractionTarget>.removeDestroyedElement(navElement: NavElement<InteractionTarget>): State<InteractionTarget> =
        copy(votedCards = votedCards.filterNot { it == navElement })

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> =
        copy(votedCards = emptyList())

    override fun State<InteractionTarget>.availableElements(): Set<NavElement<InteractionTarget>> =
        (votedCards + visibleCards + queued).map { it.navElement }.toSet()

    override fun State<InteractionTarget>.destroyedElements(): Set<NavElement<InteractionTarget>> = setOf()

}
