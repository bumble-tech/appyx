package com.bumble.appyx.components.stable.backstack.operation

import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child1
import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child2
import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child3
import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child4
import com.bumble.appyx.components.stable.backstack.InteractionTarget.Child5
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.components.stable.backstack.BackStackModel
import com.bumble.appyx.components.stable.backstack.operation.NewRoot
import kotlin.test.Test
import kotlin.test.assertEquals

class NewRootTest {

    @Test
    fun GIVEN_new_root_THEN_everything_else_is_destroyed() {
        val state = BackStackModel.State(
            active = Child1.asElement(),
            stashed = listOf(Child2, Child3, Child4).map { it.asElement() }
        )

        val newRoot = NewRoot(Child5)

        val actual = newRoot.invoke(state)

        assertEquals(
            actual = actual.targetState.active.interactionTarget,
            expected = Child5
        )

        val expectedDestroyed = listOf(Child2, Child3, Child4)

        actual.targetState.destroyed.forEachIndexed { index, element ->
            assertEquals(
                actual = element.interactionTarget,
                expected = expectedDestroyed[index]
            )
        }
    }
}
