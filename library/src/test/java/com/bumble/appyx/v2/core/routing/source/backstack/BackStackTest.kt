package com.bumble.appyx.v2.core.routing.source.backstack

import com.bumble.appyx.v2.core.node.ParentNode.Companion.KEY_ROUTING_SOURCE
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.CREATED
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Pop
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Push
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing1
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing2
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing3
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Routing.Routing4
import com.bumble.appyx.v2.core.routing.source.backstack.operation.assertBackstackElementsEqual
import com.bumble.appyx.v2.core.routing.source.backstack.operation.backStackElement
import com.bumble.appyx.v2.core.routing.source.backstack.operation.push
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
internal class BackStackTest {

    private val testScope = TestCoroutineScope()

    @After
    fun cleanUp() {
        testScope.cleanupTestCoroutines()
    }

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
                operation = Operation.Noop()
            )
        )
        initialState.assertBackstackElementsEqual(expectedElements)
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
        state.assertBackstackElementsEqual(expectedElements)
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
        state.assertBackstackElementsEqual(expectedElements)
    }

    @Test
    fun `all gets notified when change in backstack state`() {

        val initialElement = Routing1
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = null
        )

        var state: BackStackElements<Routing>? = null
        testScope.launch {
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
        state!!.assertBackstackElementsEqual(expectedElements)
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

        val canHandleBackPress = backStack.canHandleBackPress.value

        assertEquals(canHandleBackPress, true)
    }

    @Test
    fun `canHandleBackPress gets notified when change in backstack`() {

        val initialElement = Routing1
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = null
        )

        var canHandleBackPress: Boolean? = null
        testScope.launch {
            backStack
                .canHandleBackPress
                .collect { canHandleBackPress = it }
        }

        backStack.push(Routing2)

        assertEquals(canHandleBackPress, true)
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

        val canHandleBackPress = backStack.canHandleBackPress.value

        assertEquals(canHandleBackPress, false)
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
        state.assertBackstackElementsEqual(expectedElements)
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
        state.assertBackstackElementsEqual(expectedElements)
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
        state.assertBackstackElementsEqual(expectedElements)
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
        state.assertBackstackElementsEqual(expectedElements)
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
                operation = Operation.Noop(),
            )
        )
        state.assertBackstackElementsEqual(expectedElements)
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

        backStack.onBackPressed()

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
        state.assertBackstackElementsEqual(expectedElements)
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

        backStack.saveInstanceState(savedStateMap = savedStateMap)
        val actual = savedStateMap[KEY_ROUTING_SOURCE] as BackStackElements<Routing>

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

        actual.assertBackstackElementsEqual(expectedElements)
    }

    private fun <T : Any> push(element: T) = Push(
        element = element
    )

}
