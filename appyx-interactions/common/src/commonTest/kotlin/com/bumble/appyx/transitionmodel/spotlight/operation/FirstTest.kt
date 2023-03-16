package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.InteractionTarget
import com.bumble.appyx.InteractionTarget.Child1
import com.bumble.appyx.InteractionTarget.Child2
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.Position
import kotlin.test.Test
import kotlin.test.assertEquals

class FirstTest {

    @Test
    fun GIVEN_active_index_is_last_WHEN_First_is_invoked_THEN_active_index_is_0() {
        val state = SpotlightModel.State(
            positions = listOf(
                Position(elements = mapOf(Child1.asElement() to STANDARD)),
                Position(elements = mapOf(Child2.asElement() to STANDARD))
            ),
            activeIndex = 1f
        )
        val first = First<InteractionTarget>()

        assertEquals(
            actual = first.invoke(state).targetState.activeIndex,
            expected = 0f
        )
    }
}
