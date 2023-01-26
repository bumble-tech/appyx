package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.NavTarget
import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import com.bumble.appyx.NavTarget.Child3
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlin.test.Test
import kotlin.test.assertEquals

class LastTest {

    @Test
    fun GIVEN_active_index_is_0_WHEN_Last_is_invoked_THEN_active_index_is_last() {
        val state = SpotlightModel.State(
            standard = listOf(Child1, Child2, Child3).map { it.asElement() },
            activeIndex = 0f,
            activeWindow = 1f
        )

        val last = Last<NavTarget>()

        assertEquals(
            actual = last.invoke(state).targetState.activeIndex,
            expected = 2f
        )
    }
}
