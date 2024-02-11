package com.bumble.appyx.components.stable.backstack.operation

import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.Replace
import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child1
import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child2
import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child3
import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child4
import com.bumble.appyx.interactions.model.asElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ReplaceTest {

    @Test
    fun WHEN_replace_same_nav_target_THEN_operation_not_applicable() {
        val state = BackStackModel.State(active = Child1.asElement())

        val replace = Replace(Child1)

        assertFalse(replace.isApplicable(state))
    }

    @Test
    fun WHEN_replace_THEN_active_is_replaced() {
        val state = BackStackModel.State(
            active = Child1.asElement(),
            stashed = listOf(Child2, Child3).map { it.asElement() }
        )

        val replace = Replace(Child4)

        assertEquals(
            actual = replace(state).targetState.active.interactionTarget,
            expected = Child4
        )
    }
}
