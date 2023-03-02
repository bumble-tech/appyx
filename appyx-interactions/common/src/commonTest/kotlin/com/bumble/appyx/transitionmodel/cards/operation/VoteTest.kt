package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.NavTarget
import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import com.bumble.appyx.NavTarget.Child3
import com.bumble.appyx.NavTarget.Child4
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.cards.CardsModel
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.InvisibleCard.Queued
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.InvisibleCard.VotedCard
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.InvisibleCard.VotedCard.VOTED_CARD_STATE.LIKED
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.VisibleCard.BottomCard
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.VisibleCard.TopCard
import com.bumble.appyx.transitionmodel.cards.CardsModel.State.Card.VisibleCard.TopCard.TOP_CARD_STATE.STANDARD
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class VoteTest {

    @Test
    fun WHEN_queue_is_empty_THEN_Like_operation_is_not_applicable() {
        val state = CardsModel.State<NavTarget>()

        val voteLike = VoteLike<NavTarget>()

        assertFalse(voteLike.isApplicable(state))
    }

    @Test
    fun WHEN_queue_is_empty_THEN_Pass_operation_is_not_applicable() {
        val state = CardsModel.State<NavTarget>()

        val votePass = VotePass<NavTarget>()

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

        val voteLike = VoteLike<NavTarget>()

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
//        val votePass = VotePass<NavTarget>()
//
//        val actual = votePass.invoke(state)
//
//        val expectedLiked = listOf(Child1)
//        actual.targetState.liked.forEachIndexed { index, element ->
//            assertEquals(
//                actual = element.navTarget,
//                expected = expectedLiked[index]
//            )
//        }
//
//        val expectedQueued = listOf(Child2)
//        actual.targetState.queued.forEachIndexed { index, element ->
//            assertEquals(
//                actual = element.navTarget,
//                expected = expectedQueued[index]
//            )
//        }
//    }
}
