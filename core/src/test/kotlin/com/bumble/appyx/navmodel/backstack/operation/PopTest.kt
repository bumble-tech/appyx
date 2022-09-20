package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.navmodel.assertNavTargetElementsEqual
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.STASHED
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget1
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget2
import org.junit.Assert.assertEquals
import org.junit.Test

internal class PopTest {

    @Test
    fun `not applicable when no stashed element`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Pop<NavTarget>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `not applicable when no on screen element`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED,
                targetState = STASHED,
                operation = Noop()
            )
        )
        val operation = Pop<NavTarget>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `applicable when on screen and stashed elements present`() {

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
            ),
        )
        val operation = Pop<NavTarget>()

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `destroys current element on screen and add on screen last stashed element`() {

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
        val operation = Pop<NavTarget>()

        val newElements = operation.invoke(elements = elements)

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
}
