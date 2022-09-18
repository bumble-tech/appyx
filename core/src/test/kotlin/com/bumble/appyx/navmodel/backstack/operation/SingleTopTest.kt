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
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

internal class SingleTopTest {

    @Test
    fun `applicable when no element of same type`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = SingleTop.init(
            element = NavTarget2,
            elements = elements
        )

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `not applicable when one element of same type and same content but current element on screen same as referenced element`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget4("Content"),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = SingleTop.init(
            element = NavTarget4("Content"),
            elements = elements
        )

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, false)
    }

    @Test
    fun `applicable when one element of same type and same content and current element on screen different than referenced element`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = SingleTop.init(
            element = NavTarget4("Content"),
            elements = elements
        )

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `applicable when one element of same type and different content`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget4("Content 1"),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = SingleTop.init(
            element = NavTarget4("Content 2"),
            elements = elements
        )

        val applicable = operation.isApplicable(elements)

        assertEquals(applicable, true)
    }

    @Test
    fun `performs a push when no element of same type`() {

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
        val operation = SingleTop.init(
            element = NavTarget3,
            elements = elements
        )

        val newElements = operation.invoke(elements)

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

    @Test
    fun `crashes when one element of same type and same content but no element on screen`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget4("Content 1"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            )
        )
        val operation = SingleTop.init(
            element = NavTarget4("Content 1"),
            elements = elements
        )

        assertThrows(IllegalArgumentException::class.java) {
            operation.invoke(elements)
        }
    }

    @Test
    fun `destroys current element on screen and reactivate chosen one`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget4("Content 1"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = SingleTop.init(
            element = NavTarget4("Content 1"),
            elements = elements
        )

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget4("Content 1"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = ACTIVE,
                operation = operation
            ),
            backStackElement(
                element = NavTarget3,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = operation
            )
        )
        newElements.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `crashes when one element of same type and different content but no element on screen`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget4("Content 1"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            )
        )
        val operation = SingleTop.init(
            element = NavTarget4("Content 2"),
            elements = elements
        )

        assertThrows(IllegalArgumentException::class.java) {
            operation.invoke(elements)
        }
    }

    @Test
    fun `destroys current element on screen and activate new instance of chosen one`() {

        val elements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget4("Content 1"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        val operation = SingleTop.init(
            element = NavTarget4("Content 2"),
            elements = elements
        )

        val newElements = operation.invoke(elements)

        val expectedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Noop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = operation
            ),
            backStackElement(
                element = NavTarget4("Content 2"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = operation
            )
        )
        newElements.assertNavTargetElementsEqual(expectedElements)
    }
}
