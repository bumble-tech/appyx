package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import com.bumble.appyx.transitionmodel.NavTarget.Child3
import com.bumble.appyx.transitionmodel.NavTarget.Child4
import com.bumble.appyx.transitionmodel.NavTarget.Child5
import com.bumble.appyx.transitionmodel.NavTarget.Child6
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateElementsTest {

    @Test
    fun `When new elements are provided Then the new ones are standard`() {
        val old = listOf(Child1, Child2, Child3)
        val state = SpotlightModel.State(
            standard = old.map { it.asElement() },
            activeIndex = 1f,
            activeWindow = 2f
        )

        val updateElements = UpdateElements(listOf(Child4, Child5, Child6))

        assertEquals(
            actual = updateElements.invoke(state).targetState.destroyed.map { it.navTarget },
            expected = old
        )
    }

    @Test
    fun `When new elements are provided Then the old ones are destroyed`() {
        val old = listOf(Child1, Child2, Child3)
        val state = SpotlightModel.State(
            standard = old.map { it.asElement() },
            activeIndex = 1f,
            activeWindow = 2f
        )

        val new = listOf(Child4, Child5, Child6)
        val updateElements = UpdateElements(new)

        assertEquals(
            actual = updateElements.invoke(state).targetState.standard.map { it.navTarget },
            expected = new
        )
    }

    @Test
    fun `When new elements are provided Then the index and window size are preserved`() {
        val old = listOf(Child1, Child2, Child3)
        val state = SpotlightModel.State(
            standard = old.map { it.asElement() },
            activeIndex = 1f,
            activeWindow = 2f
        )

        val new = listOf(Child4, Child5, Child6)
        val updateElements = UpdateElements(new)

        val finalState = updateElements.invoke(state)

        assertEquals(
            actual = finalState.targetState.activeIndex,
            expected = 1f
        )

        assertEquals(
            actual = finalState.targetState.activeWindow,
            expected = 2f
        )
    }
}
