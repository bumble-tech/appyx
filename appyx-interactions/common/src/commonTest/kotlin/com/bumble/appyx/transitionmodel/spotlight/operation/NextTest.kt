package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.NavTarget
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import com.bumble.appyx.transitionmodel.NavTarget.Child3
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NextTest {

    @Test
    fun `Given on last position When next is invoked Then operation isn't applicable`() {
        val state = SpotlightModel.State(
            standard = listOf(Child1, Child2, Child3).map { it.asElement() },
            activeIndex = 2f,
            activeWindow = 1f
        )

        val next = Next<NavTarget>()

        assertFalse(next.isApplicable(state))
    }

    @Test
    fun `Given on first position And has next When next invoked Then it will increment the active index`() {
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
