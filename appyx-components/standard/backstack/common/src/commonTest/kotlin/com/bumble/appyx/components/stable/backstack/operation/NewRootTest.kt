package com.bumble.appyx.components.stable.backstack.operation

import com.bumble.appyx.components.stable.backstack.TestTarget.Child1
import com.bumble.appyx.components.stable.backstack.TestTarget.Child2
import com.bumble.appyx.components.stable.backstack.TestTarget.Child3
import com.bumble.appyx.components.stable.backstack.TestTarget.Child4
import com.bumble.appyx.components.stable.backstack.TestTarget.Child5
import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.NewRoot
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
