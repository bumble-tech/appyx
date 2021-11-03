package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.*
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.*
import org.junit.Assert.assertEquals
import org.junit.Test

internal class PushTest {

    @Test
    fun `not applicable when current element on screen same as pushed element`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
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
                uuid = 1,
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
                uuid = 1,
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
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = Push<Routing>(element = Routing3)

        val newElements = operation.invoke(elements = elements, uuidGenerator = UuidGenerator(2))

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = CREATED,
                targetState = ON_SCREEN
            )
        )
        assertEquals(newElements, expectedElements)
    }
}
