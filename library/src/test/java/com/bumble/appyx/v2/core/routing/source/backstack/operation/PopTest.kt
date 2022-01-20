package com.bumble.appyx.v2.core.routing.source.backstack.operation

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElement
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing1
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing2
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
                operation = Operation.Noop()
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
                operation = Operation.Noop()
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
                operation = Operation.Noop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Operation.Noop()
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
                operation = Operation.Noop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Operation.Noop()
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
        newElements.assertBackstackElementsEqual(expectedElements)
    }
}
