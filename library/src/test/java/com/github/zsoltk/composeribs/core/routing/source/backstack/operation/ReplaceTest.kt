package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.CREATED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.DESTROYED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.ON_SCREEN
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing1
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing2
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

internal class ReplaceTest {

    @Test
    fun `not applicable when current element on screen same as referenced element`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
                operation = Operation.Noop()
            )
        )
        val operation = Replace<Routing>(element = Routing1)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `applicable when current element on screen different than referenced element`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
                operation = Operation.Noop()
            )
        )
        val operation = Replace<Routing>(element = Routing2)

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
        val operation = Replace<Routing>(element = Routing2)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `crashes when no element on screen`() {

        val elements = emptyList<BackStackElement<Routing>>()
        val operation = Replace<Routing>(element = Routing1)

        assertThrows(IllegalArgumentException::class.java) {
            operation.invoke(elements)
        }
    }

    @Test
    fun `destroys current element on screen and add on screen the newly created one`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Operation.Noop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
                operation = Operation.Noop()
            )
        )

        val operation = Replace<Routing>(element = Routing3)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Operation.Noop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = operation
            ),
            backStackElement(
                element = Routing3,
                fromState = CREATED,
                targetState = ON_SCREEN,
                operation = operation
            )
        )
        newElements.assertBackstackElementsEqual(expectedElements)
    }
}
