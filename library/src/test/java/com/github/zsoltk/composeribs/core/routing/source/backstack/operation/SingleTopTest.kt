package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.CREATED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.DESTROYED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.ON_SCREEN
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing1
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing2
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing3
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

internal class SingleTopTest {

    @Test
    fun `applicable when no element of same type`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = SingleTop.init(element = Routing2, elements = elements)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `not applicable when one element of same type and same content but current element on screen same as referenced element`() {

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
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = SingleTop.init(element = Routing4("Content"), elements = elements)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `applicable when one element of same type and same content and current element on screen different than referenced element`() {

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
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = SingleTop.init(element = Routing4("Content"), elements = elements)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `applicable when one element of same type and different content`() {

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
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing4("Content 1"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = SingleTop.init(element = Routing4("Content 2"), elements = elements)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `performs a push when no element of same type`() {

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
        val operation = SingleTop.init(element = Routing3, elements = elements)

        val newElements = operation.invoke(elements, UuidGenerator(2))

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

    @Test
    fun `crashes when one element of same type and same content but no element on screen`() {

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
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing4("Content 1"),
                uuid = 4,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val operation = SingleTop.init(Routing4("Content 1"), elements = elements)

        assertThrows(IllegalArgumentException::class.java) {
            operation.invoke(elements, UuidGenerator(2))
        }
    }

    @Test
    fun `destroys current element on screen and reactivate chosen one`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing4("Content 1"),
                uuid = 4,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = SingleTop.init(element = Routing4("Content 1"), elements = elements)

        val newElements = operation.invoke(elements, UuidGenerator(4))

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing4("Content 1"),
                uuid = 4,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = ON_SCREEN,
                targetState = DESTROYED
            )
        )
        assertEquals(newElements, expectedElements)
    }

    @Test
    fun `crashes when one element of same type and different content but no element on screen`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing4("Content 1"),
                uuid = 4,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val operation = SingleTop.init(element = Routing4("Content 2"), elements = elements)

        assertThrows(IllegalArgumentException::class.java) {
            operation.invoke(elements, UuidGenerator(2))
        }
    }

    @Test
    fun `destroys current element on screen and activate new instance of chosen one`() {

        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing4("Content 1"),
                uuid = 4,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = SingleTop.init(element = Routing4("Content 2"), elements)

        val newElements = operation.invoke(elements, UuidGenerator(4))

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = ON_SCREEN,
                targetState = DESTROYED
            ),
            backStackElement(
                element = Routing4("Content 2"),
                uuid = 5,
                fromState = CREATED,
                targetState = ON_SCREEN
            )
        )
        assertEquals(newElements, expectedElements)
    }
}
