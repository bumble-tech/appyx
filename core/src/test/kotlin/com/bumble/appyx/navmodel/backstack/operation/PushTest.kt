package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.navmodel.assertNavTargetElementsEqual
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.CREATED
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget1
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget2
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget3
import org.junit.Assert.assertEquals
import org.junit.Test

internal class PushTest {

    @Test
    fun `not applicable when current element on screen same as pushed element`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Push<NavTarget>(element = NavTarget1)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `applicable when current element on screen different than pushed element`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = Push<NavTarget>(element = NavTarget2)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `applicable when no element on screen`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            )
        )
        val operation = Push<NavTarget>(element = NavTarget2)

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `stashes current element on screen and add on screen the newly created one`() {

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
        val operation = Push<NavTarget>(element = NavTarget3)

        val newElements = operation.invoke(elements = elements)

        val expectedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
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
