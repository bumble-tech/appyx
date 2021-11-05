package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.DESTROYED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.ON_SCREEN
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing1
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

internal class RemoveTest {

    @Test
    fun `not applicable when key not found`() {

        val key = backStackKey<Routing>(
            element = Routing1,
            uuid = 2
        )
        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = Remove(key = key)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `not applicable when key found but element to be destroyed`() {

        val key = backStackKey<Routing>(
            element = Routing1,
            uuid = 1
        )
        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = ON_SCREEN,
                targetState = DESTROYED
            )
        )
        val operation = Remove(key = key)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `applicable when key found and element not to be destroyed`() {

        val key = backStackKey<Routing>(
            element = Routing1,
            uuid = 1
        )
        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = Remove(key = key)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `does nothing when key not found`() {

        val key = backStackKey<Routing>(
            element = Routing2,
            uuid = 3
        )
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
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements, UuidGenerator(0))

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
                targetState = ON_SCREEN
            )
        )
        assertEquals(newElements, expectedElements)
    }

    @Test
    fun `does nothing when key found but element to be destroyed`() {

        val key = backStackKey<Routing>(
            element = Routing2,
            uuid = 2
        )
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
                targetState = DESTROYED
            )
        )
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements, UuidGenerator(0))

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
                targetState = DESTROYED
            )
        )
        assertEquals(newElements, expectedElements)
    }

    @Test
    fun `crashes when item to remove on screen but no element stashed`() {

        val key = backStackKey<Routing>(
            element = Routing1,
            uuid = 1
        )
        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val operation = Remove(key = key)

        assertThrows(IllegalArgumentException::class.java) {
            operation.invoke(elements, UuidGenerator(0))
        }
    }

    @Test
    fun `destroys current element on screen and add on screen last stashed element`() {

        val key = backStackKey<Routing>(
            element = Routing2,
            uuid = 2
        )
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
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements, UuidGenerator(0))

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = ON_SCREEN,
                targetState = DESTROYED
            )
        )
        assertEquals(newElements, expectedElements)
    }

    @Test
    fun `silently removes item when not on screen`() {

        val key = backStackKey<Routing>(
            element = Routing1,
            uuid = 1
        )
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
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements, UuidGenerator(0))

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        assertEquals(newElements, expectedElements)
    }
}
