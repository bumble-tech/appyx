package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.NavTarget
import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PopTest {

    @Test
    fun GIVEN_no_stashed_elements_THEN_it_is_not_applicable() {
        val state = BackStackModel.State(active = NavElement(Child1))

        val pop = Pop<NavTarget>()

        assertFalse(pop.isApplicable(state))
    }

    @Test
    fun GIVEN_active_and_stashed_elements_THEN_destroys_active_and_makes_stashed_active() {
        val state = BackStackModel.State(
            active = NavElement(Child1),
            stashed = listOf(NavElement(Child2))
        )

        val pop = Pop<NavTarget>()

        val actual = pop.invoke(state)

        assertEquals(
            actual = actual.targetState.active.navTarget,
            expected = Child2
        )

        val expectedDestroyed = listOf(Child1)
        actual.targetState.destroyed.forEachIndexed { index, element ->
            assertEquals(
                actual = element.navTarget,
                expected = expectedDestroyed[index]
            )
        }
    }
}
