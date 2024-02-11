package com.bumble.appyx.components.spotlight.model

import com.bumble.appyx.components.spotlight.TestTarget.Child1
import com.bumble.appyx.components.spotlight.TestTarget.Child2
import com.bumble.appyx.components.spotlight.TestTarget.Child3
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.Next
import com.bumble.appyx.components.spotlight.operation.UpdateElements
import com.bumble.appyx.interactions.core.state.MutableSavedStateMapImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class SpotlightModelTest {

    @Test
    fun GIVEN_spotlight_with_2_elements_WHEN_state_restored_THEN_all_elements_are_retained() {
        val savedStateMap = mutableMapOf<String, Any?>()
        val spotlight = SpotlightModel(
            items = listOf(Child1, Child2),
            savedStateMap = savedStateMap
        )

        spotlight.operation(Next())

        val state = MutableSavedStateMapImpl(savedStateMap) { true }
        spotlight.saveInstanceState(state)

        val newSpotlight = SpotlightModel(
            items = listOf(Child1, Child2),
            savedStateMap = state.savedState
        )

        assertEquals(
            spotlight.output.value.currentTargetState,
            newSpotlight.output.value.currentTargetState
        )
    }

    @Test
    fun GIVEN_empty_spotlight_WHEN_updated_with_element_THEN_state_contains_elements() {
        val spotlight = SpotlightModel(
            items = listOf(),
            savedStateMap = null
        )

        val newElements = listOf(Child1, Child2)
        val newActiveIndex = 1f

        spotlight.operation(
            UpdateElements(
                items = newElements,
                initialActiveIndex = newActiveIndex
            )
        )

        assertEquals(
            expected = newElements,
            actual = spotlight.elements.value.map { it.interactionTarget }.toList(),
        )
    }

    @Test
    fun GIVEN_empty_spotlight_WHEN_updated_with_element_THEN_sets_provided_active_index() {
        val spotlight = SpotlightModel(
            items = listOf(),
            savedStateMap = null
        )

        val newElements = listOf(Child1, Child2)
        val newActiveIndex = 1f

        spotlight.operation(
            UpdateElements(
                items = newElements,
                initialActiveIndex = newActiveIndex
            )
        )

        assertEquals(
            expected = newActiveIndex,
            actual = spotlight.output.value.currentTargetState.activeIndex,
        )
    }

    @Test
    fun GIVEN_empty_spotlight_WHEN_updated_with_element_THEN_provides_correct_activeElement() {
        val spotlight = SpotlightModel(
            items = listOf(),
            savedStateMap = null
        )

        val newElements = listOf(Child1, Child2)
        val newActiveIndex = 1f

        spotlight.operation(
            UpdateElements(
                items = newElements,
                initialActiveIndex = newActiveIndex
            )
        )

        assertEquals(
            expected = Child2,
            actual = spotlight.output.value.currentTargetState.activeElement,
        )
    }

    @Test
    fun GIVEN_spotlight_WHEN_updated_with_elements_different_size_THEN_state_contains_element() {
        val spotlight = SpotlightModel(
            items = listOf(Child1),
            savedStateMap = null
        )

        val newElements = listOf(Child2, Child3)

        spotlight.operation(UpdateElements(items = newElements))

        assertEquals(
            expected = newElements,
            actual = spotlight.elements.value.map { it.interactionTarget }.toList(),
        )
    }
}
