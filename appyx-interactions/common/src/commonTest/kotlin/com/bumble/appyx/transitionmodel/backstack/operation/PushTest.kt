package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PushTest {

    @Test
    fun GIVEN_Child1_on_screen_WHEN_Child1_is_pushed_again_THEN_operation_is_not_applicable() {
        val state = BackStackModel.State(active = Child1.asElement())

        val push = Push(Child1)

        assertFalse(push.isApplicable(state))
    }

    @Test
    fun GIVEN_Child_1_on_screen_WHEN_Child2_is_pushed_THEN_Child1_will_be_stashed_and_Child2_active() {
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
