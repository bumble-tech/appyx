package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import com.bumble.appyx.transitionmodel.NavTarget.Child3
import com.bumble.appyx.transitionmodel.NavTarget.Child4
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ReplaceTest {

    @Test
    fun `When replace same nav target Then operation not applicable`() {
        val state = BackStackModel.State(active = Child1.asElement())

        val replace = Replace(Child1)

        assertFalse(replace.isApplicable(state))
    }

    @Test
    fun `When replace then active is replaced`() {
        val state = BackStackModel.State(
            active = Child1.asElement(),
            stashed = listOf(Child2, Child3).map { it.asElement() }
        )

        val replace = Replace(Child4)

        assertEquals(
            actual = replace(state).targetState.active.navTarget,
            expected = Child4
        )
    }
}
