package com.bumble.appyx.components.spotlight.operation

import com.bumble.appyx.components.spotlight.InteractionTarget
import com.bumble.appyx.components.spotlight.InteractionTarget.Child1
import com.bumble.appyx.components.spotlight.InteractionTarget.Child2
import com.bumble.appyx.components.spotlight.InteractionTarget.Child3
import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.components.spotlight.SpotlightModel.State.Position
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ActivateTest {

    @Test
    fun WHEN_new_index_is_the_same_THEN_operation_is_not_applicable() {
        val state = SpotlightModel.State(
            positions = listOf(
                Position(elements = mapOf(Child1.asElement() to STANDARD)),
                Position(elements = mapOf(Child2.asElement() to STANDARD))
            ),
            activeIndex = 0f,
        )

        val activate = Activate<InteractionTarget>(0f)

        assertFalse(activate.isApplicable(state))
    }

    @Test
    fun WHEN_new_index_is_different_AND_out_of_range_THEN_operation_is_not_applicable() {
        val state = SpotlightModel.State(
            positions = listOf(
                Position(elements = mapOf(Child1.asElement() to STANDARD)),
                Position(elements = mapOf(Child2.asElement() to STANDARD))
            ),
            activeIndex = 0f
        )

        val activate = Activate<InteractionTarget>(2f)

        assertFalse(activate.isApplicable(state))
    }

    @Test
    fun WHEN_new_index_is_different_AND_is_withing_range_THEN_active_index_is_updated() {
        val state = SpotlightModel.State(
            positions = listOf(
                Position(elements = mapOf(Child1.asElement() to STANDARD)),
                Position(elements = mapOf(Child2.asElement() to STANDARD)),
                Position(elements = mapOf(Child3.asElement() to STANDARD)),
            ),
            activeIndex = 0f
        )

        val activate = Activate<InteractionTarget>(1f)

        assertEquals(
            actual = activate.invoke(state).targetState.activeIndex,
            expected = 1f
        )
    }
}
