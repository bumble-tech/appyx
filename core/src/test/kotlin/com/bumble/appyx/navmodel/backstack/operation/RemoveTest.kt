package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.assertNavTargetElementsEqual
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.STASHED
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget1
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

internal class RemoveTest {

    @Test
    fun `not applicable when key not found`() {

        val key = NavKey<NavTarget>(navTarget = NavTarget1)
        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Remove(key = key)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `not applicable when key found but element to be destroyed`() {

        val key = NavKey<NavTarget>(navTarget = NavTarget1)
        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Noop()
            )
        )
        val operation = Remove(key = key)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `applicable when key found and element not to be destroyed`() {

        val key = NavKey<NavTarget>(navTarget = NavTarget1)
        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                key = key,
                element = NavTarget1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Remove(key = key)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `does nothing when key not found`() {
        val key = NavKey<NavTarget>(navTarget = NavTarget2)

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED,
                targetState = STASHED,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED,
                targetState = STASHED,
                operation = Noop()
            ),
            backStackElement(
                key = key,
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        newElements.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `does nothing when key found but element to be destroyed`() {

        val key = NavKey<NavTarget>(navTarget = NavTarget2)

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED,
                targetState = STASHED,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Noop()
            )
        )
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED,
                targetState = STASHED,
                operation = Noop()
            ),
            backStackElement(
                key = key,
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Noop()
            )
        )
        newElements.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `crashes when item to remove on screen but no element stashed`() {

        val key = NavKey<NavTarget>(
            navTarget = NavTarget1
        )
        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                key = key,
                element = NavTarget1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Remove(key = key)

        assertThrows(IllegalArgumentException::class.java) {
            operation.invoke(elements)
        }
    }

    @Test
    fun `destroys current element on screen and add on screen last stashed element`() {

        val key = NavKey<NavTarget>(
            navTarget = NavTarget2
        )
        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED,
                targetState = STASHED,
                operation = Noop()
            ),
            backStackElement(
                key = key,
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED,
                targetState = ACTIVE,
                operation = operation
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = operation
            )
        )
        newElements.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `silently removes item when not on screen`() {

        val key = NavKey<NavTarget>(navTarget = NavTarget1)
        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                key = key,
                element = NavTarget1,
                fromState = STASHED,
                targetState = STASHED,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Remove(key = key)

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        newElements.assertNavTargetElementsEqual(expectedElements)
    }
}
