package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.InteractionTarget
import com.bumble.appyx.InteractionTarget.Child1
import com.bumble.appyx.InteractionTarget.Child2
import com.bumble.appyx.InteractionTarget.Child3
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.Position
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NextTest {

    @Test
    fun GIVEN_on_last_position_WHEN_next_is_invoked_THEN_operation_is_not_applicable() {
        val state = SpotlightModel.State(
            positions = listOf(
                Position(elements = mapOf(Child1.asElement() to STANDARD)),
                Position(elements = mapOf(Child2.asElement() to STANDARD)),
                Position(elements = mapOf(Child3.asElement() to STANDARD))
            ),
            activeIndex = 2f
        )

        val next = Next<InteractionTarget>()

        assertFalse(next.isApplicable(state))
    }

    @Test
    fun GIVEN_on_first_position_AND_has_next_WHEN_next_invoked_THEN_it_will_increment_the_active_index() {
        val state = SpotlightModel.State(
            positions = listOf(
                Position(elements = mapOf(Child1.asElement() to STANDARD)),
                Position(elements = mapOf(Child2.asElement() to STANDARD)),
                Position(elements = mapOf(Child3.asElement() to STANDARD))
            ),
            activeIndex = 0f
        )

        val next = Next<InteractionTarget>()

        assertEquals(
            actual = next.invoke(state).targetState.activeIndex,
            expected = 1f
        )
    }
}
