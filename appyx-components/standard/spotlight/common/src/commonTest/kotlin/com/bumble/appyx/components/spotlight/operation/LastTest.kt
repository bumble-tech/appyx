package com.bumble.appyx.components.spotlight.operation

import com.bumble.appyx.components.spotlight.InteractionTarget
import com.bumble.appyx.components.spotlight.InteractionTarget.Child1
import com.bumble.appyx.components.spotlight.InteractionTarget.Child2
import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.components.spotlight.SpotlightModel.State.Position
import kotlin.test.Test
import kotlin.test.assertEquals

class LastTest {

    @Test
    fun GIVEN_active_index_is_0_WHEN_Last_is_invoked_THEN_active_index_is_last() {
        val state = SpotlightModel.State(
            positions = listOf(
                Position(elements = mapOf(Child1.asElement() to STANDARD)),
                Position(elements = mapOf(Child2.asElement() to STANDARD))
            ),
            activeIndex = 0f
        )
        val last = Last<InteractionTarget>()

        assertEquals(
            actual = last.invoke(state).targetState.activeIndex,
            expected = 1f
        )
    }
}
