package com.bumble.appyx.routingsource.backstack

import com.bumble.appyx.core.routing.BaseRoutingSource.Companion.KEY_ROUTING_SOURCE
import com.bumble.appyx.core.routing.Operation.Noop
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.routingsource.assertRoutingElementsEqual
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState.CREATED
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.bumble.appyx.routingsource.backstack.operation.Pop
import com.bumble.appyx.routingsource.backstack.operation.Push
import com.bumble.appyx.routingsource.backstack.operation.Routing
import com.bumble.appyx.routingsource.backstack.operation.Routing.Routing1
import com.bumble.appyx.routingsource.backstack.operation.Routing.Routing2
import com.bumble.appyx.routingsource.backstack.operation.Routing.Routing3
import com.bumble.appyx.routingsource.backstack.operation.Routing.Routing4
import com.bumble.appyx.routingsource.backstack.operation.backStackElement
import com.bumble.appyx.routingsource.backstack.operation.push
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

        val initialElement = Routing1
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = null
        )

        val initialState = backStack.elements.value

        val expectedElements: BackStackElements<Routing> = listOf(
            BackStackElement(
                key = RoutingKey(initialElement),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        )
        initialState.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `state should correspond to restored state when state to be restored`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.elements.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        state.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `all returns all the backstack elements`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.elements.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        state.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `all gets notified when change in backstack state`() = testScope.runTest {

        val initialElement = Routing1
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = null
        )

        var state: BackStackElements<Routing>? = null
        val job = launch {
            backStack
                .elements
                .collect { state = it }
        }

        backStack.push(Routing2)

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing1,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = push(Routing2)
            ),
            backStackElement(
                element = Routing2,
                fromState = CREATED,
                targetState = ACTIVE,
                operation = push(Routing2)
            )
        )
        job.cancel()
        state!!.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `canHandleBackPress returns true when stashed elements present`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val canHandleBackPress = backStack.onBackPressedCallback.isEnabled

        assertEquals(true, canHandleBackPress)
    }

    @Test
    fun `canHandleBackPress returns false when no stashed elements`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ACTIVE,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val canHandleBackPress = backStack.onBackPressedCallback.isEnabled

        assertEquals(false, canHandleBackPress)
    }

    @Test
    fun `when transition of item to be destroyed is finished then it is removed from state`() {

        val initialElement = Routing1
        val transitionedItemKey: RoutingKey<Routing> = RoutingKey(
            routing = Routing3
        )
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                key = transitionedItemKey,
                element = Routing3,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )


        backStack.onTransitionFinished(transitionedItemKey)

        val state = backStack.elements.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            )
        )
        state.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `when transition of item to be stashed is finished then its state is changed`() {
        val initialElement = Routing1
        val transitionedItemKey: RoutingKey<Routing> = RoutingKey(routing = Routing2)
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                key = transitionedItemKey,
                element = Routing2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        backStack.onTransitionFinished(transitionedItemKey)

        val state = backStack.elements.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        state.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `when transition of item to get on screen is finished then its state is changed`() {

        val initialElement = Routing1
        val transitionedItemKey: RoutingKey<Routing> = RoutingKey(
            routing = Routing4("Content")
        )
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                key = transitionedItemKey,
                element = Routing4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )
        backStack.onTransitionFinished(transitionedItemKey)

        val state = backStack.elements.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        state.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `an operation is performed when it is applicable`() {

        val initialElement = Routing1
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = null
        )

        val operation = Push<Routing>(Routing2)
        backStack.accept(operation)

        val state = backStack.elements.value

        val expectedElements: BackStackElements<Routing> = listOf(
            BackStackElement(
                key = RoutingKey(initialElement),
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = operation
            ),
            BackStackElement(
                key = RoutingKey(Routing2),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = operation
            )
        )
        state.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `an operation is not performed when it is not applicable`() {

        val initialElement = Routing1
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = null
        )

        val operation = Push<Routing>(Routing1)
        backStack.accept(operation)

        val state = backStack.elements.value

        val expectedElements: BackStackElements<Routing> = listOf(
            BackStackElement(
                key = RoutingKey(initialElement),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop(),
            )
        )
        state.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `pops element on screen when back pressed`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        backStack.handleOnBackPressed()

        val state = backStack.elements.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ACTIVE,
                operation = Pop()
            )
        )
        state.assertRoutingElementsEqual(expectedElements)
    }

    @Test
    fun `when saving instance state stores the elements with idle transition`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                fromState = CREATED,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = ACTIVE,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ACTIVE,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val writer = MutableSavedStateMapImpl { true }
        backStack.saveInstanceState(writer)
        val actual = writer.savedState[KEY_ROUTING_SOURCE] as BackStackElements<Routing>

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )

        actual.assertRoutingElementsEqual(expectedElements)
    }

    private fun <T : Any> push(element: T) = Push(
        element = element
    )

}
