package com.bumble.appyx.components.experimental.cards.operation

import com.bumble.appyx.components.experimental.cards.CardsModel
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.InvisibleCard.Queued
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.InvisibleCard.VotedCard
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.InvisibleCard.VotedCard.VOTED_CARD_STATE.LIKED
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.VisibleCard.BottomCard
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.VisibleCard.TopCard
import com.bumble.appyx.components.experimental.cards.CardsModel.State.Card.VisibleCard.TopCard.TOP_CARD_STATE.STANDARD
import com.bumble.appyx.components.experimental.cards.InteractionTarget
import com.bumble.appyx.components.experimental.cards.InteractionTarget.Child1
import com.bumble.appyx.components.experimental.cards.InteractionTarget.Child2
import com.bumble.appyx.components.experimental.cards.InteractionTarget.Child3
import com.bumble.appyx.components.experimental.cards.InteractionTarget.Child4
import com.bumble.appyx.interactions.core.asElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class VoteTest {

    @Test
    fun WHEN_queue_is_empty_THEN_Like_operation_is_not_applicable() {
        val state = CardsModel.State<InteractionTarget>()

        val voteLike = VoteLike<InteractionTarget>()

        assertFalse(voteLike.isApplicable(state))
    }

    @Test
    fun WHEN_queue_is_empty_THEN_Pass_operation_is_not_applicable() {
        val state = CardsModel.State<InteractionTarget>()

        val votePass = VotePass<InteractionTarget>()

        assertFalse(votePass.isApplicable(state))
    }

    @Test
    fun GIVEN_queue_contains_more_elements_WHEN_vote_like_Then_element_moved_to_liked() {
        val state = CardsModel.State(
            visibleCards = listOf(
                TopCard(
                    element = Child1.asElement(),
                    topCardState = STANDARD
                ),
                BottomCard(element = Child2.asElement())
            ),
            queued = listOf(Child3, Child4).map { Queued(it.asElement()) }
        )

        val voteLike = VoteLike<InteractionTarget>()

        val actual = voteLike.invoke(state)


        // check voted card is liked
        assertEquals(actual.targetState.votedCards.size, 1)
        val votedCard = actual.targetState.votedCards[0] as VotedCard
        assertEquals(
            actual = votedCard.element.interactionTarget,
            expected = Child1
        )
        assertEquals(
            actual = votedCard.votedCardState,
            expected = LIKED
        )
    }

    // TODO fix
//    @Test
//    fun GIVEN_queue_contains_more_elements_WHEN_vote_pass_THEN_element_moved_to_passed() {
//        val state = CardsModel.State(
//            queued = listOf(Child1, Child2).map { it.asElement() }
//        )
//
//        val votePass = VotePass<InteractionTarget>()
//
//        val actual = votePass.invoke(state)
//
//        val expectedLiked = listOf(Child1)
//        actual.targetState.liked.forEachIndexed { index, element ->
//            assertEquals(
//                actual = element.interactionTarget,
//                expected = expectedLiked[index]
//            )
//        }
//
//        val expectedQueued = listOf(Child2)
//        actual.targetState.queued.forEachIndexed { index, element ->
//            assertEquals(
//                actual = element.interactionTarget,
//                expected = expectedQueued[index]
//            )
//        }
//    }
}
