package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.NavTarget
import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import com.bumble.appyx.NavTarget.Child3
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NextTest {

    @Test
    fun GIVEN_on_last_position_WHEN_next_is_invoked_THEN_operation_is_not_applicable() {
        val state = SpotlightModel.State(
            standard = listOf(Child1, Child2, Child3).map { it.asElement() },
            activeIndex = 2f,
            activeWindow = 1f
        )

        val next = Next<NavTarget>()

        assertFalse(next.isApplicable(state))
    }

    @Test
    fun GIVEN_on_first_position_AND_has_next_WHEN_next_invoked_THEN_it_will_increment_the_active_index() {
        val state = SpotlightModel.State(
            standard = listOf(Child1, Child2, Child3).map { it.asElement() },
            activeIndex = 0f,
            activeWindow = 1f
        )

        val next = Next<NavTarget>()

        assertEquals(
            actual = next.invoke(state).targetState.activeIndex,
            expected = 1f
        )
    }
}
