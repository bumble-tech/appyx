package com.bumble.appyx.components.spotlight.operation

import com.bumble.appyx.components.spotlight.TestTarget.Child1
import com.bumble.appyx.components.spotlight.TestTarget.Child2
import com.bumble.appyx.components.spotlight.TestTarget.Child3
import com.bumble.appyx.components.spotlight.TestTarget.Child4
import com.bumble.appyx.components.spotlight.TestTarget.Child5
import com.bumble.appyx.components.spotlight.TestTarget.Child6
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.components.spotlight.SpotlightModel.State.Position
import com.bumble.appyx.interactions.core.asElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateElementsTest {

    @Test
    fun WHEN_new_elements_are_provided_THEN_the_new_ones_are_standard() {
        val state = SpotlightModel.State(
            positions = listOf(
                Position(elements = mapOf(Child1.asElement() to STANDARD)),
                Position(elements = mapOf(Child2.asElement() to STANDARD))
            ),
            activeIndex = 0f
        )

        val updateElements = UpdateElements(listOf(Child3, Child4))

        val finalState = updateElements.invoke(state).targetState

        val actualItems = finalState.positions
            .flatMap { it.elements.entries }
            .filter { it.value == STANDARD }
            .map { it.key.interactionTarget }
            .toSet()

        assertTrue(actualItems.contains(Child3))
        assertTrue(actualItems.contains(Child4))
    }

    @Test
    fun WHEN_new_elements_are_provided_THEN_the_old_ones_are_destroyed() {
        val state = SpotlightModel.State(
            positions = listOf(
                Position(elements = mapOf(Child1.asElement() to STANDARD)),
                Position(elements = mapOf(Child2.asElement() to STANDARD))
            ),
            activeIndex = 0f
        )

        val updateElements = UpdateElements(listOf(Child3, Child4))

        val finalState = updateElements.invoke(state).targetState

        val actualItems = finalState.positions
            .flatMap { it.elements.entries }
            .filter { it.value == DESTROYED }
            .map { it.key.interactionTarget }
            .toSet()

        assertTrue(actualItems.contains(Child1))
        assertTrue(actualItems.contains(Child2))
    }

    @Test
    fun WHEN_new_elements_are_provided_THEN_the_index_and_window_size_are_preserved() {
        val state = SpotlightModel.State(
            positions = listOf(
                Position(elements = mapOf(Child1.asElement() to STANDARD)),
                Position(elements = mapOf(Child2.asElement() to STANDARD)),
                Position(elements = mapOf(Child3.asElement() to STANDARD))
            ),
            activeIndex = 2f
        )

        val updateElements = UpdateElements(listOf(Child4, Child5, Child6))

        val finalState = updateElements.invoke(state)

        assertEquals(
            actual = finalState.targetState.activeIndex,
            expected = 2f
        )
    }
}
