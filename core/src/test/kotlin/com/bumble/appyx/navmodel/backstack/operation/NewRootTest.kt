package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.navmodel.assertNavTargetElementsEqual
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.CREATED
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget1
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget2
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

internal class NewRootTest {

    @Test
    fun `is always applicable`() {

        val elements = emptyList<BackStackElement<NavTarget>>()
        val operation = NewRoot<NavTarget>(element = NavTarget1)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `crashes when no element on screen`() {

        val elements = emptyList<BackStackElement<NavTarget>>()
        val operation = NewRoot<NavTarget>(element = NavTarget1)

        assertThrows(IllegalArgumentException::class.java) {
            operation.invoke(elements)
        }
    }

    @Test
    fun `passed element becomes the only one when currently on screen`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )

        val operation = NewRoot<NavTarget>(element = NavTarget2)

        val newElements = operation.invoke(elements = elements)

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

    @Test
    fun `destroys current element on screen, remove the rest, and add on screen the newly created one`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )

        val operation = NewRoot<NavTarget>(element = NavTarget3)

        val newElements = operation.invoke(elements = elements)

        val expectedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = operation
            ),
            backStackElement(
                element = NavTarget3,
                fromState = CREATED,
                targetState = ACTIVE,
                operation = operation
            )
        )
        newElements.assertNavTargetElementsEqual(expectedElements)
    }
}
