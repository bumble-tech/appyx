package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import com.bumble.appyx.NavTarget.Child3
import com.bumble.appyx.NavTarget.Child4
import com.bumble.appyx.NavTarget.Child5
import com.bumble.appyx.NavTarget.Child6
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateElementsTest {

    @Test
    fun WHEN_new_elements_are_provided_THEN_the_new_ones_are_standard() {
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
    fun WHEN_new_elements_are_provided_THEN_the_old_ones_are_destroyed() {
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
    fun WHEN_new_elements_are_provided_THEN_the_index_and_window_size_are_preserved() {
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
