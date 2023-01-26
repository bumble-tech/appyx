package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.NavTarget
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import com.bumble.appyx.transitionmodel.cards.CardsModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class VoteTest {

    @Test
    fun `When queue is empty Then Like operation is not applicable`() {
        val state = CardsModel.State<NavTarget>()

        val voteLike = VoteLike<NavTarget>()

        assertFalse(voteLike.isApplicable(state))
    }

    @Test
    fun `When queue is empty Then Pass operation is not applicable`() {
        val state = CardsModel.State<NavTarget>()

        val votePass = VotePass<NavTarget>()

        assertFalse(votePass.isApplicable(state))
    }

    @Test
    fun `Given queue contains more elements When vote like Then element moved to liked`() {
        val state = CardsModel.State(
            queued = listOf(Child1, Child2).map { it.asElement() }
        )

        val voteLike = VoteLike<NavTarget>()

        val actual = voteLike.invoke(state)

        val expectedLiked = listOf(Child1)
        actual.targetState.liked.forEachIndexed { index, element ->
            assertEquals(
                actual = element.navTarget,
                expected = expectedLiked[index]
            )
        }

        val expectedQueued = listOf(Child2)
        actual.targetState.queued.forEachIndexed { index, element ->
            assertEquals(
                actual = element.navTarget,
                expected = expectedQueued[index]
            )
        }
    }

    @Test
    fun `Given queue contains more elements When vote pass Then element moved to passed`() {
        val state = CardsModel.State(
            queued = listOf(Child1, Child2).map { it.asElement() }
        )

        val votePass = VotePass<NavTarget>()

        val actual = votePass.invoke(state)

        val expectedLiked = listOf(Child1)
        actual.targetState.liked.forEachIndexed { index, element ->
            assertEquals(
                actual = element.navTarget,
                expected = expectedLiked[index]
            )
        }

        val expectedQueued = listOf(Child2)
        actual.targetState.queued.forEachIndexed { index, element ->
            assertEquals(
                actual = element.navTarget,
                expected = expectedQueued[index]
            )
        }
    }
}
