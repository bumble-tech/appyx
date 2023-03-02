package com.bumble.appyx.transitionmodel.cards

import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.InvisibleCard.Queued
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.VisibleCard.BottomCard
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.VisibleCard.TopCard

class CardsModel<NavTarget : Any>(
    initialItems: List<NavTarget> = listOf(),
) : BaseTransitionModel<NavTarget, CardsModel.State<NavTarget>>() {

    data class State<NavTarget>(
        val votedCards: List<Card.InvisibleCard<NavTarget>> = emptyList(),
        val visibleCards: List<Card.VisibleCard<NavTarget>> = emptyList(),
        val queued: List<Card.InvisibleCard<NavTarget>> = emptyList()
    ) {
        sealed class Card<NavTarget> {
            abstract val navElement: NavElement<NavTarget>

            sealed class VisibleCard<NavTarget> : Card<NavTarget>() {
                data class TopCard<NavTarget>(
                    override val navElement: NavElement<NavTarget>,
                    val topCardState: TOP_CARD_STATE
                ) : VisibleCard<NavTarget>() {
                    enum class TOP_CARD_STATE {
                        STANDARD, INDICATE_LIKE, INDICATE_PASS
                    }
                }

                data class BottomCard<NavTarget>(
                    override val navElement: NavElement<NavTarget>
                ) : VisibleCard<NavTarget>()
            }

            sealed class InvisibleCard<NavTarget> : Card<NavTarget>() {

                data class Queued<NavTarget>(
                    override val navElement: NavElement<NavTarget>
                ) : InvisibleCard<NavTarget>()

                data class VotedCard<NavTarget>(
                    override val navElement: NavElement<NavTarget>,
                    val votedCardState: VOTED_CARD_STATE
                ) : InvisibleCard<NavTarget>() {
                    enum class VOTED_CARD_STATE {
                        LIKED, PASSED
                    }
                }
            }
        }
    }

    fun getInitialState(initialItems: List<NavTarget>): State<NavTarget> {
        val visibleCards: MutableList<State.Card.VisibleCard<NavTarget>> = mutableListOf()
        val queuedCards: MutableList<State.Card.InvisibleCard<NavTarget>> = mutableListOf()
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


    override val initialState: State<NavTarget> = getInitialState(initialItems)

    override fun State<NavTarget>.removeDestroyedElement(navElement: NavElement<NavTarget>): State<NavTarget> =
        copy(votedCards = votedCards.filterNot { it == navElement })

    override fun State<NavTarget>.removeDestroyedElements(): State<NavTarget> =
        copy(votedCards = emptyList())

    override fun State<NavTarget>.availableElements(): Set<NavElement<NavTarget>> =
        (votedCards + visibleCards + queued).map { it.navElement }.toSet()

    override fun State<NavTarget>.destroyedElements(): Set<NavElement<NavTarget>> = setOf()

}
