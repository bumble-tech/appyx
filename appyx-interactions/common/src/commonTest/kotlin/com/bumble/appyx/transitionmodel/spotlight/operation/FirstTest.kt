package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.NavTarget
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import com.bumble.appyx.transitionmodel.NavTarget.Child3
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlin.test.Test
import kotlin.test.assertEquals

class FirstTest {

    @Test
    fun `Given active index is last When First is invoked Then active index is 0`() {
        val state = SpotlightModel.State(
            standard = listOf(Child1, Child2, Child3).map { it.asElement() },
            activeIndex = 2f,
            activeWindow = 1f
        )

        val first = First<NavTarget>()

        assertEquals(
            actual = first.invoke(state).targetState.activeIndex,
            expected = 0f
        )
    }
}
