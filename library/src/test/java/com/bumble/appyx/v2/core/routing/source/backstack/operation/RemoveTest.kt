package com.bumble.appyx.v2.core.routing.source.backstack.operation

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElement
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing1
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

internal class RemoveTest {

    @Test
    fun `not applicable when key not found`() {

        val key = RoutingKey<Routing>(routing = Routing1)
        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Operation.Noop()
            )
        )
        val operation = Remove(key = key)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `not applicable when key found but element to be destroyed`() {

        val key = RoutingKey<Routing>(routing = Routing1)
        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Operation.Noop()
            )
        )
        val operation = Remove(key = key)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `applicable when key found and element not to be destroyed`() {

        val key = RoutingKey<Routing>(routing = Routing1)
        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                key = key,
                element = Routing1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Operation.Noop()
            )
        )
        val operation = Remove(key = key)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `does nothing when key not found`() {
        val key = RoutingKey<Routing>(routing = Routing2)

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
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Operation.Noop()
            ),
            backStackElement(
                key = key,
                element = Routing2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Operation.Noop()
            )
        )
        newElements.assertBackstackElementsEqual(expectedElements)
    }

    @Test
    fun `does nothing when key found but element to be destroyed`() {

        val key = RoutingKey<Routing>(routing = Routing2)

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
                targetState = DESTROYED,
                operation = Operation.Noop()
            )
        )
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Operation.Noop()
            ),
            backStackElement(
                key = key,
                element = Routing2,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Operation.Noop()
            )
        )
        newElements.assertBackstackElementsEqual(expectedElements)
    }

    @Test
    fun `crashes when item to remove on screen but no element stashed`() {

        val key = RoutingKey<Routing>(
            routing = Routing1
        )
        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                key = key,
                element = Routing1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Operation.Noop()
            )
        )
        val operation = Remove(key = key)

        assertThrows(IllegalArgumentException::class.java) {
            operation.invoke(elements)
        }
    }

    @Test
    fun `destroys current element on screen and add on screen last stashed element`() {

        val key = RoutingKey<Routing>(
            routing = Routing2
        )
        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Operation.Noop()
            ),
            backStackElement(
                key = key,
                element = Routing2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Operation.Noop()
            )
        )
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements)

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

    @Test
    fun `silently removes item when not on screen`() {

        val key = RoutingKey<Routing>(routing = Routing1)
        val elements = listOf<BackStackElement<Routing>>(
            backStackElement(
                key = key,
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
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Operation.Noop()
            )
        )
        newElements.assertBackstackElementsEqual(expectedElements)
    }
}
