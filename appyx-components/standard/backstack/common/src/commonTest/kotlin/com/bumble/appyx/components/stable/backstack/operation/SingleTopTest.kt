package com.bumble.appyx.components.stable.backstack.operation

import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.SingleTop
import com.bumble.appyx.components.stable.backstack.TestTarget
import com.bumble.appyx.components.stable.backstack.TestTarget.Child1
import com.bumble.appyx.components.stable.backstack.TestTarget.Child2
import com.bumble.appyx.components.stable.backstack.TestTarget.Child3
import com.bumble.appyx.components.stable.backstack.TestTarget.Child4
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.model.Elements
import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.interactions.model.transition.StateTransition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SingleTopTest {

    @Test
    fun GIVEN_no_stashed_elements_THEN_it_is_applicable() {
        val state = BackStackModel.State(active = Child1.asElement())

        val singleTop = SingleTop(Child2)

        assertTrue(singleTop.isApplicable(state))
    }

    @Test
    fun GIVEN_some_stashed_elements_THEN_it_is_applicable() {
        val state = BackStackModel.State(
            active = Child1.asElement(),
            stashed = elements(Child2)
        )

        val singleTop = SingleTop(Child3)

        assertTrue(singleTop.isApplicable(state))
    }

    @Test
    fun GIVEN_active_and_different_stashed_elements_THEN_makes_new_item_active_and_moves_old_active_to_stashed() {
        val state = BackStackModel.State(
            active = Child3.asElement(),
            stashed = elements(Child1, Child2)
        )

        val actual = SingleTop(Child4).invoke(state)

        actual.assertActive(Child4)

        actual.assertStashed(Child1, Child2, Child3)
    }

    @Test
    fun GIVEN_stashed_element_same_THEN_destroys_current_active_and_all_stashed_after_it_and_makes_matching_stashed_new_active() {
        val state = BackStackModel.State(
            active = Child4.asElement(),
            stashed = elements(Child1, Child2, Child3)
        )

        val actual = SingleTop(Child2).invoke(state)

        actual.assertActive(Child2)

        actual.assertStashed(Child1)

        actual.assertDestroyed(Child3, Child4)
    }

    @Test
    fun GIVEN_active_element_same_THEN_does_nothing() {
        val state = BackStackModel.State(
            active = Child4.asElement(),
            stashed = elements(Child1, Child2, Child3)
        )

        val actual = SingleTop(Child4).invoke(state)

        actual.assertActive(Child4)

        actual.assertStashed(Child1, Child2, Child3)

        actual.assertDestroyed()
    }

    @Test
    fun GIVEN_nothing_stashed_and_active_element_same_THEN_does_nothing() {
        val state = BackStackModel.State(
            active = Child1.asElement(),
            stashed = emptyList(),
        )

        val actual = SingleTop(Child1).invoke(state)

        actual.assertActive(Child1)

        actual.assertStashed()

        actual.assertDestroyed()
    }

    private fun StateTransition<BackStackModel.State<TestTarget>>.assertActive(expected: TestTarget) {
        assertEquals(
            actual = targetState.active.interactionTarget,
            expected = expected,
            message = "Active",
        )
    }

    private fun StateTransition<BackStackModel.State<TestTarget>>.assertStashed(vararg expected: TestTarget) {
        assertSame(
            actual = targetState.stashed,
            expected = expected.toList(),
            message = "Stashed",
        )
    }

    private fun StateTransition<BackStackModel.State<TestTarget>>.assertDestroyed(vararg expected: TestTarget) {
        assertSame(
            actual = targetState.destroyed,
            expected = expected.toList(),
            message = "Destroyed",
        )
    }

    private fun elements(vararg elements: TestTarget): Elements<TestTarget> =
        elements.toList().map { it.asElement() }

    private fun assertSame(actual: Elements<TestTarget>, expected: List<TestTarget>, message: String) {
        assertEquals(
            actual = actual.map { it.interactionTarget }.toList(),
            expected = expected,
            message = message,
        )
    }
}
