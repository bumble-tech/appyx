package com.github.zsoltk.composeribs.core.routing.source.backstack

import com.github.zsoltk.composeribs.core.node.ParentNode.Companion.KEY_ROUTING_SOURCE
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.CREATED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.DESTROYED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.ON_SCREEN
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Pop
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Push
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing1
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing2
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing3
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.Routing4
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.assertBackstackElementsEqual
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.backStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.push
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

        val initialState = backStack.all.value

        val expectedElements: BackStackElements<Routing> = listOf(
            BackStackElement(
                BackStackOnScreenResolver,
                key = RoutingKey(initialElement),
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
                onScreen = true,
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

        val state = backStack.all.value

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
                targetState = ON_SCREEN,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.all.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = ON_SCREEN,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
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
                .all
                .collect { state = it }
        }

        backStack.push(Routing2)

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing1,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK,
                operation = push(Routing2)
            ),
            backStackElement(
                element = Routing2,
                fromState = CREATED,
                targetState = ON_SCREEN,
                operation = push(Routing2)
            )
        )
        state!!.assertBackstackElementsEqual(expectedElements)
    }

    @Test
    fun `offScreen returns only the offScreen elements`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ON_SCREEN,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.offScreen.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        state.assertBackstackElementsEqual(expectedElements)
    }

    @Test
    fun `offScreen gets notified when change in backstack state regarding offscreen elements`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        var state: BackStackElements<Routing>? = null
        testScope.launch {
            backStack
                .offScreen
                .collect { state = it }
        }

        backStack.push(Routing1)

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        state!!.assertBackstackElementsEqual(expectedElements)
    }

    @Test
    fun `onScreen returns only the onScreen elements`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ON_SCREEN,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.onScreen.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing3,
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = Routing1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ON_SCREEN,
                operation = Pop()
            )
        )
        state.assertBackstackElementsEqual(expectedElements)
    }

    @Test
    fun `onScreen gets notified when change in backstack state regarding onScreen elements`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            )
        )
        val savedStateMap = mutableMapOf<String, Any>(KEY_ROUTING_SOURCE to storedElements)
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        var state: BackStackElements<Routing>? = null
        testScope.launch {
            backStack
                .onScreen
                .collect { state = it }
        }

        backStack.push(Routing3)

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing3,
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = CREATED,
                targetState = ON_SCREEN,
                operation = push(Routing3)
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
                fromState = ON_SCREEN,
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
                targetState = ON_SCREEN,
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
                targetState = ON_SCREEN,
                operation = Pop()
            ),
            backStackElement(
                key = transitionedItemKey,
                element = Routing3,
                fromState = ON_SCREEN,
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

        val state = backStack.all.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = CREATED,
                targetState = ON_SCREEN,
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
                targetState = ON_SCREEN,
                operation = Pop()
            ),
            backStackElement(
                key = transitionedItemKey,
                element = Routing2,
                fromState = ON_SCREEN,
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

        val state = backStack.all.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = CREATED,
                targetState = ON_SCREEN,
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
                targetState = ON_SCREEN,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
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

        val state = backStack.all.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
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

        val operation = DummyClearOperation(isApplicable = true)
        backStack.perform(operation)

        val state = backStack.all.value

        val expectedState = emptyList<BackStackElements<Routing>>()
        assertEquals(state, expectedState)
    }

    @Test
    fun `an operation is not performed when it is not applicable`() {

        val initialElement = Routing1
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = null
        )

        val operation = DummyClearOperation(isApplicable = false)
        backStack.perform(operation)

        val state = backStack.all.value

        val expectedElements: BackStackElements<Routing> = listOf(
            BackStackElement(
                BackStackOnScreenResolver,
                key = RoutingKey(initialElement),
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
                operation = Operation.Noop(),
                onScreen = true
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
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
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

        val state = backStack.all.value

        val expectedElements: BackStackElements<Routing> = listOf(
            backStackElement(
                element = Routing4("Content"),
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ON_SCREEN,
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
                targetState = ON_SCREEN,
                operation = Pop()
            ),
            backStackElement(
                element = Routing3,
                fromState = ON_SCREEN,
                targetState = DESTROYED,
                operation = Pop()
            ),
            backStackElement(
                element = Routing2,
                fromState = ON_SCREEN,
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
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
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

    @Test
    fun `returns true when element on screen`() {
        val initialElement = Routing1
        val key: RoutingKey<Routing> = RoutingKey(
            routing = Routing4("Content")
        )
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                key = key,
                element = Routing4("Content"),
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
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

        val isOnScreen = backStack.isOnScreen(key = key)

        assertEquals(isOnScreen, true)
    }

    @Test
    fun `returns false when element not on screen`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
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
        val key: RoutingKey<Routing> = RoutingKey(
            routing = Routing2
        )

        val isOnScreen = backStack.isOnScreen(key = key)

        assertEquals(isOnScreen, false)
    }

    private fun <T : Any> push(element: T) = Push(
        onScreenResolver = BackStackOnScreenResolver,
        element = element
    )

    private class DummyClearOperation(
        private val isApplicable: Boolean
    ) : Operation<Routing, BackStack.TransitionState> {

        override fun isApplicable(elements: BackStackElements<Routing>): Boolean = isApplicable

        override fun invoke(elements: BackStackElements<Routing>): BackStackElements<Routing> =
            emptyList()
    }
}
