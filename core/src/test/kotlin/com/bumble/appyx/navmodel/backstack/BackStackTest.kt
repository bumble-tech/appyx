package com.bumble.appyx.navmodel.backstack

import com.bumble.appyx.core.navigation.BaseNavModel.Companion.KEY_NAV_MODEL
import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.assertNavTargetElementsEqual
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.CREATED
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.bumble.appyx.navmodel.backstack.operation.Pop
import com.bumble.appyx.navmodel.backstack.operation.Push
import com.bumble.appyx.navmodel.backstack.operation.NavTarget
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget1
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget2
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget3
import com.bumble.appyx.navmodel.backstack.operation.NavTarget.NavTarget4
import com.bumble.appyx.navmodel.backstack.operation.backStackElement
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.core.state.MutableSavedStateMapImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
internal class BackStackTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Test
    fun `initial state should include initial element and have it on screen`() {

        val initialElement = NavTarget1
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = null
        )

        val initialState = backStack.elements.value

        val expectedElements: BackStackElements<NavTarget> = listOf(
            BackStackElement(
                key = NavKey(initialElement),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        initialState.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `state should correspond to restored state when state to be restored`() {

        val initialElement = NavTarget1
        val storedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_NAV_MODEL to storedElements)
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.elements.value

        val expectedElements: BackStackElements<NavTarget> = listOf(
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        state.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `all returns all the backstack elements`() {

        val initialElement = NavTarget1
        val storedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_NAV_MODEL to storedElements)
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.elements.value

        val expectedElements: BackStackElements<NavTarget> = listOf(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        state.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `all gets notified when change in backstack state`() = testScope.runTest {

        val initialElement = NavTarget1
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = null
        )

        var state: BackStackElements<NavTarget>? = null
        val job = launch {
            backStack
                .elements
                .collect { state = it }
        }

        backStack.push(NavTarget2)

        val expectedElements: BackStackElements<NavTarget> = listOf(
            backStackElement(
                element = NavTarget1,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = push(NavTarget2)
            ),
            backStackElement(
                element = NavTarget2,
                fromState = CREATED,
                targetState = ACTIVE,
                operation = push(NavTarget2)
            )
        )
        job.cancel()
        state!!.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `canHandleBackPress returns true when stashed elements present`() {

        val initialElement = NavTarget1
        val storedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_NAV_MODEL to storedElements)
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val canHandleBackPress = backStack.onBackPressedCallback.isEnabled

        assertEquals(true, canHandleBackPress)
    }

    @Test
    fun `canHandleBackPress returns false when no stashed elements`() {

        val initialElement = NavTarget1
        val storedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ACTIVE,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_NAV_MODEL to storedElements)
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val canHandleBackPress = backStack.onBackPressedCallback.isEnabled

        assertEquals(false, canHandleBackPress)
    }

    @Test
    fun `when transition of item to be destroyed is finished then it is removed from state`() {

        val initialElement = NavTarget1
        val transitionedItemKey: NavKey<NavTarget> = NavKey(
            navTarget = NavTarget3
        )
        val storedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                key = transitionedItemKey,
                element = NavTarget3,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_NAV_MODEL to storedElements)
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )


        backStack.onTransitionFinished(transitionedItemKey)

        val state = backStack.elements.value

        val expectedElements: BackStackElements<NavTarget> = listOf(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            )
        )
        state.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `when transition of item to be stashed is finished then its state is changed`() {
        val initialElement = NavTarget1
        val transitionedItemKey: NavKey<NavTarget> = NavKey(navTarget = NavTarget2)
        val storedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                key = transitionedItemKey,
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_NAV_MODEL to storedElements)
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        backStack.onTransitionFinished(transitionedItemKey)

        val state = backStack.elements.value

        val expectedElements: BackStackElements<NavTarget> = listOf(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        state.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `when transition of item to get on screen is finished then its state is changed`() {

        val initialElement = NavTarget1
        val transitionedItemKey: NavKey<NavTarget> = NavKey(
            navTarget = NavTarget4("Content")
        )
        val storedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                key = transitionedItemKey,
                element = NavTarget4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_NAV_MODEL to storedElements)
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )
        backStack.onTransitionFinished(transitionedItemKey)

        val state = backStack.elements.value

        val expectedElements: BackStackElements<NavTarget> = listOf(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        state.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `an operation is performed when it is applicable`() {

        val initialElement = NavTarget1
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = null
        )

        val operation = Push<NavTarget>(NavTarget2)
        backStack.accept(operation)

        val state = backStack.elements.value

        val expectedElements: BackStackElements<NavTarget> = listOf(
            BackStackElement(
                key = NavKey(initialElement),
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = operation
            ),
            BackStackElement(
                key = NavKey(NavTarget2),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = operation
            )
        )
        state.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `an operation is not performed when it is not applicable`() {

        val initialElement = NavTarget1
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = null
        )

        val operation = Push<NavTarget>(NavTarget1)
        backStack.accept(operation)

        val state = backStack.elements.value

        val expectedElements: BackStackElements<NavTarget> = listOf(
            BackStackElement(
                key = NavKey(initialElement),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop(),
            )
        )
        state.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `pops element on screen when back pressed`() {

        val initialElement = NavTarget1
        val storedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_NAV_MODEL to storedElements)
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        backStack.handleOnBackPressed()

        val state = backStack.elements.value

        val expectedElements: BackStackElements<NavTarget> = listOf(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ACTIVE,
                operation = Pop()
            )
        )
        state.assertNavTargetElementsEqual(expectedElements)
    }

    @Test
    fun `when saving instance state stores the elements with idle transition`() {

        val initialElement = NavTarget1
        val storedElements = listOf<BackStackElement<NavTarget>>(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget3,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_NAV_MODEL to storedElements)
        val backStack = BackStack<NavTarget>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val writer = MutableSavedStateMapImpl { true }
        backStack.saveInstanceState(writer)
        val actual = writer.savedState[KEY_NAV_MODEL] as BackStackElements<NavTarget>

        val expectedElements: BackStackElements<NavTarget> = listOf(
            backStackElement(
                element = NavTarget4("Content"),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = NavTarget2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )

        actual.assertNavTargetElementsEqual(expectedElements)
    }

    private fun <T : Any> push(element: T) = Push(
        element = element
    )

}
