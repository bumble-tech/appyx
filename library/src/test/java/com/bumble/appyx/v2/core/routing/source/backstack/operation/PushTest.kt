package com.bumble.appyx.v2.core.routing.source.backstack.operation

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.CREATED
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElement
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing1
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing2
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing3
import org.junit.Assert.assertEquals
import org.junit.Test

internal class PushTest {

    @Test
    fun `not applicable when current element on screen same as pushed element`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Operation.Noop()
            )
        )
        val operation = Push<Routing>(element = Routing1)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `applicable when current element on screen different than pushed element`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Operation.Noop()
            )
        )
        val operation = Push<Routing>(element = Routing2)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `applicable when no element on screen`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Operation.Noop()
            )
        )
        val operation = Push<Routing>(element = Routing2)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `stashes current element on screen and add on screen the newly created one`() {

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
        val operation = Push<Routing>(element = Routing3)

        val newElements = operation.invoke(elements = elements)

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Operation.Noop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = operation
            ),
            backStackElement(
                element = Routing3,
                fromState = CREATED,
                targetState = ACTIVE,
                operation = operation
            )
        )
        newElements.assertBackstackElementsEqual(expectedElements)
    }

}
