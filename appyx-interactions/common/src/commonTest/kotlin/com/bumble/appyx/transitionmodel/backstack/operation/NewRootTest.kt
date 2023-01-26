package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import com.bumble.appyx.transitionmodel.NavTarget.Child3
import com.bumble.appyx.transitionmodel.NavTarget.Child4
import com.bumble.appyx.transitionmodel.NavTarget.Child5
import kotlin.test.Test
import kotlin.test.assertEquals

class NewRootTest {

    @Test
    fun `Given new root Then everything else is destroyed`() {
        val state = BackStackModel.State(
            active = Child1.asElement(),
            stashed = listOf(Child2, Child3, Child4).map { it.asElement() }
        )

        val newRoot = NewRoot(Child5)

        val actual = newRoot.invoke(state)

        assertEquals(
            actual = actual.targetState.active.navTarget,
            expected = Child5
        )

        val expectedDestroyed = listOf(Child2, Child3, Child4)

        actual.targetState.destroyed.forEachIndexed { index, element ->
            assertEquals(
                actual = element.navTarget,
                expected = expectedDestroyed[index]
            )
        }
    }
}
