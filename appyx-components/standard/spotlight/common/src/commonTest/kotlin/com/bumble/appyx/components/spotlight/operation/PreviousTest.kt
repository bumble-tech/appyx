package com.bumble.appyx.components.spotlight.operation

import com.bumble.appyx.components.spotlight.TestTarget.Child1
import com.bumble.appyx.components.spotlight.TestTarget.Child2
import com.bumble.appyx.components.spotlight.TestTarget.Child3
import com.bumble.appyx.components.spotlight.TestTarget.Child4
import com.bumble.appyx.components.spotlight.TestTarget.Child5
import com.bumble.appyx.components.spotlight.TestTarget.Child6
import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.TestTarget
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PreviousTest {

    @Test
    fun GIVEN_on_first_position_WHEN_previous_is_invoked_THEN_operation_is_not_applicable() {
        val state = SpotlightModel.State(
            positions = listOf(
                SpotlightModel.State.Position(
                    mapOf(
                        Child1.asElement() to SpotlightModel.State.ElementState.STANDARD,
                        Child2.asElement() to SpotlightModel.State.ElementState.STANDARD,
                        Child3.asElement() to SpotlightModel.State.ElementState.STANDARD,
                    )
                ),
                SpotlightModel.State.Position(
                    mapOf(
                        Child4.asElement() to SpotlightModel.State.ElementState.STANDARD,
                        Child5.asElement() to SpotlightModel.State.ElementState.STANDARD,
                        Child6.asElement() to SpotlightModel.State.ElementState.STANDARD,
                    )
                )
            ),
            activeIndex = 0f
        )

        val previous = Previous<TestTarget>()

        assertFalse(previous.isApplicable(state))
    }

    @Test
    fun GIVEN_on_last_position_AND_has_previous_WHEN_previous_invoked_THEN_it_will_decrement_the_active_index() {
        val state = SpotlightModel.State(
            positions = listOf(
                SpotlightModel.State.Position(
                    mapOf(
                        Child1.asElement() to SpotlightModel.State.ElementState.STANDARD,
                        Child2.asElement() to SpotlightModel.State.ElementState.STANDARD,
                        Child3.asElement() to SpotlightModel.State.ElementState.STANDARD,
                    )
                ),
                SpotlightModel.State.Position(
                    mapOf(
                        Child4.asElement() to SpotlightModel.State.ElementState.STANDARD,
                        Child5.asElement() to SpotlightModel.State.ElementState.STANDARD,
                        Child6.asElement() to SpotlightModel.State.ElementState.STANDARD,
                    )
                )
            ),
            activeIndex = 2f
        )

        val previous = Previous<TestTarget>()

        assertEquals(
            actual = previous.invoke(state).targetState.activeIndex,
            expected = 1f
        )
    }
}
