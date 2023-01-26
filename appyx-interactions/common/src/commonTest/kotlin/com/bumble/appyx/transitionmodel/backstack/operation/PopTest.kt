package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.transitionmodel.NavTarget
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PopTest {

    @Test
    fun `Given no stashed elements Then it's not applicable`() {
        val state = BackStackModel.State(active = NavElement(Child1))

        val pop = Pop<NavTarget>()

        assertFalse(pop.isApplicable(state))
    }

    @Test
    fun `Given active and stashed elements Then destroys active and makes stashed active`() {
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
