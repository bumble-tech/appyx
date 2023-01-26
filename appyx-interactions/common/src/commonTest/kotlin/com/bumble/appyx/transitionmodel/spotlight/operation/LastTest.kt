package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.NavTarget
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import com.bumble.appyx.transitionmodel.NavTarget.Child3
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlin.test.Test
import kotlin.test.assertEquals

class LastTest {

    @Test
    fun `Given active index is 0 When Last is invoked Then active index is last`() {
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
