package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.CREATED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.ON_SCREEN
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing1
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing2
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing3
import org.junit.Assert.assertEquals
import org.junit.Test

internal class PushTest {

    @Test
    fun `not applicable when current element on screen same as pushed element`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
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
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
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
                targetState = STASHED_IN_BACK_STACK
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
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = Push<Routing>(element = Routing3)

        val newElements = operation.invoke(elements = elements)

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing3,
                fromState = CREATED,
                targetState = ON_SCREEN
            )
        )
        newElements.assertBackstackElementsEqual(expectedElements)
    }
}
