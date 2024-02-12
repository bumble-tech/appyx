package com.bumble.appyx.components.stable.backstack.operation

import com.bumble.appyx.components.backstack.operation.Pop
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.stable.backstack.TestTarget
import com.bumble.appyx.components.stable.backstack.TestTarget.Child1
import com.bumble.appyx.components.stable.backstack.TestTarget.Child2
import com.bumble.appyx.interactions.model.Element
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PopTest {

    @Test
    fun GIVEN_no_stashed_elements_THEN_it_is_not_applicable() {
        val state = BackStackModel.State(active = Element(Child1))

        val pop = Pop<TestTarget>()

        assertFalse(pop.isApplicable(state))
    }

    @Test
    fun GIVEN_active_and_stashed_elements_THEN_destroys_active_and_makes_stashed_active() {
        val state = BackStackModel.State(
            active = Element(Child1),
            stashed = listOf(Element(Child2))
        )

        val pop = Pop<TestTarget>()

        val actual = pop.invoke(state)

        assertEquals(
            actual = actual.targetState.active.interactionTarget,
            expected = Child2
        )

        val expectedDestroyed = listOf(Child1)
        actual.targetState.destroyed.forEachIndexed { index, element ->
            assertEquals(
                actual = element.interactionTarget,
                expected = expectedDestroyed[index]
            )
        }
    }
}
