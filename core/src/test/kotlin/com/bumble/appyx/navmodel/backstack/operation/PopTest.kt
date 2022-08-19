package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.navmodel.assertRoutingElementsEqual
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.operation.Routing.Routing1
import com.bumble.appyx.navmodel.backstack.operation.Routing.Routing2
import org.junit.Assert.assertEquals
import org.junit.Test

internal class PopTest {

    @Test
    fun `not applicable when no stashed element`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Pop<Routing>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `not applicable when no on screen element`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            )
        )
        val operation = Pop<Routing>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `applicable when on screen and stashed elements present`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            ),
        )
        val operation = Pop<Routing>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `destroys current element on screen and add on screen last stashed element`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Pop<Routing>()

        val newElements = operation.invoke(elements = elements)

        val expectedElements = listOf(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ACTIVE,
                operation = operation
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = operation
            )
        )
        newElements.assertRoutingElementsEqual(expectedElements)
    }
}
