package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.NavTarget
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PreviousTest {

    @Test
    fun `Given on first position When previous is invoked Then operation isn't applicable`() {
        val state = SpotlightModel.State(
            standard = listOf(NavTarget.Child1, NavTarget.Child2, NavTarget.Child3).map { it.asElement() },
            activeIndex = 0f,
            activeWindow = 1f
        )

        val previous = Previous<NavTarget>()

        assertFalse(previous.isApplicable(state))
    }

    @Test
    fun `Given on last position And has previous When previous invoked Then it will decrement the active index`() {
        val state = SpotlightModel.State(
            standard = listOf(NavTarget.Child1, NavTarget.Child2, NavTarget.Child3).map { it.asElement() },
            activeIndex = 2f,
            activeWindow = 1f
        )

        val previous = Previous<NavTarget>()

        assertEquals(
            actual = previous.invoke(state).targetState.activeIndex,
            expected = 1f
        )
    }
}
