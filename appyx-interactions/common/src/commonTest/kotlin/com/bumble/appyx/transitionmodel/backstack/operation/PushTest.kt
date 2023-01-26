package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PushTest {

    @Test
    fun `Given Child1 on screen When Child1 is pushed again Then operation isn't applicable`() {
        val state = BackStackModel.State(active = Child1.asElement())

        val push = Push(Child1)

        assertFalse(push.isApplicable(state))
    }

    @Test
    fun `Given Child 1 on screen When Child2 is pushed Then Child1 will be stashed and Child2 active`() {
        val state = BackStackModel.State(active = Child1.asElement())

        val push = Push(Child2)

        val actual = push.invoke(state)

        assertEquals(
            actual = actual.targetState.active.navTarget,
            expected = Child2
        )

        val expectedStashed = listOf(Child1)
        actual.targetState.stashed.forEachIndexed { index, element ->
            assertEquals(
                actual = element.navTarget,
                expected = expectedStashed[index]
            )
        }
    }
}
