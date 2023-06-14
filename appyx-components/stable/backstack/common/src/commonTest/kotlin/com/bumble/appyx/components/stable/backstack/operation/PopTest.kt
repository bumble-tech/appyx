package com.bumble.appyx.components.stable.backstack.operation

import com.bumble.appyx.components.stable.backstack.BackStackModel
import com.bumble.appyx.components.stable.backstack.InteractionTarget
import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child1
import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child2
import com.bumble.appyx.interactions.core.Element
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PopTest {

    @Test
    fun GIVEN_no_stashed_elements_THEN_it_is_not_applicable() {
        val state = BackStackModel.State(active = Element(Child1))

        val pop = Pop<InteractionTarget>()

        assertFalse(pop.isApplicable(state))
    }

    @Test
    fun GIVEN_active_and_stashed_elements_THEN_destroys_active_and_makes_stashed_active() {
        val state = BackStackModel.State(
            active = Element(Child1),
            stashed = listOf(Element(Child2))
        )

        val pop = Pop<InteractionTarget>()

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
