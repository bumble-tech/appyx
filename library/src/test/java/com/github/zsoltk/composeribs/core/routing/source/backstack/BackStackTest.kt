package com.github.zsoltk.composeribs.core.routing.source.backstack

import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.LocalRoutingKey
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.Operation
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.*
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Push
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Routing.*
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.backStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.backStackKey
import org.junit.Assert.assertEquals
import org.junit.Test

internal class BackStackTest {

    @Test
    fun `uuids should start from 1`() {

        val initialElement = Routing1
        val backStack = BackStack(
            initialElement = initialElement,
            savedStateMap = null
        )

        val newUuid = (backStack.all.value.last().key as LocalRoutingKey).uuid
        assertEquals(newUuid, 1)
    }

    @Test
    fun `uuids should start from the value after the max restored uuid`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val newElement = Routing3
        backStack.perform(Push(newElement))

        val newUuid = (backStack.all.value.last().key as LocalRoutingKey).uuid
        assertEquals(newUuid, 3)
    }

    @Test
    fun `initial state should include initial element and have it on screen`() {

        val initialElement = Routing1
        val backStack = BackStack(
            initialElement = initialElement,
            savedStateMap = null
        )

        val initialState = backStack.all.value
        val expectedState = listOf<BackStackElement<Routing>>(
            BackStackElement(
                key = backStackKey(initialElement, 1),
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
            )
        )
        assertEquals(initialState, expectedState)
    }

    @Test
    fun `state should correspond to restored state when state to be restored`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.all.value
        val expectedState = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            ),
            backStackElement(
                element = Routing1,
                uuid = 1,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        assertEquals(state, expectedState)
    }

    @Test
    fun `all returns all the backstack elements`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = DESTROYED,
                targetState = DESTROYED
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.all.value
        val expectedState = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = DESTROYED,
                targetState = DESTROYED
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        assertEquals(state, expectedState)
    }

    @Test
    fun `offScreen returns only the offScreen elements`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = DESTROYED,
                targetState = DESTROYED
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.offScreen.value
        val expectedState = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = DESTROYED,
                targetState = DESTROYED
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        assertEquals(state, expectedState)
    }

    @Test
    fun `onScreen returns only the onScreen elements`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = DESTROYED,
                targetState = DESTROYED
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val state = backStack.onScreen.value
        val expectedState = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        assertEquals(state, expectedState)
    }

    @Test
    fun `canHandleBackPress returns true when stashed elements present`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val canHandleBackPress = backStack.canHandleBackPress.value
        assertEquals(canHandleBackPress, true)
    }

    @Test
    fun `canHandleBackPress returns false when no stashed elements`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
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
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = CREATED,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = ON_SCREEN,
                targetState = DESTROYED
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val transitionedItemKey: RoutingKey<Routing> = backStackKey(
            element = Routing3,
            uuid = 3
        )
        backStack.onTransitionFinished(transitionedItemKey)

        val state = backStack.all.value
        val expectedState = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = CREATED,
                targetState = ON_SCREEN
            )
        )
        assertEquals(state, expectedState)
    }

    @Test
    fun `when transition of item to be stashed is finished then its state is changed`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = CREATED,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val transitionedItemKey: RoutingKey<Routing> = backStackKey(
            element = Routing2,
            uuid = 2
        )
        backStack.onTransitionFinished(transitionedItemKey)

        val state = backStack.all.value
        val expectedState = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = CREATED,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        assertEquals(state, expectedState)
    }

    @Test
    fun `when transition of item to get on screen is finished then its state is changed`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = CREATED,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val transitionedItemKey: RoutingKey<Routing> = backStackKey(
            element = Routing4("Content"),
            uuid = 4,
        )
        backStack.onTransitionFinished(transitionedItemKey)

        val state = backStack.all.value
        val expectedState = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        assertEquals(state, expectedState)
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
        val expectedState = emptyList<BackStackElement<Routing>>()
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
        val expectedState = listOf<BackStackElement<Routing>>(
            BackStackElement(
                key = backStackKey(initialElement, 1),
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
            )
        )
        assertEquals(state, expectedState)
    }

    @Test
    fun `pops element on screen when back pressed`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        backStack.onBackPressed()

        val state = backStack.all.value
        val expectedState = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = DESTROYED
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = ON_SCREEN
            )
        )
        assertEquals(state, expectedState)
    }

    @Test
    fun `when saving instance state returns list of elements with idle transition`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = CREATED,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing3,
                uuid = 3,
                fromState = ON_SCREEN,
                targetState = DESTROYED
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = ON_SCREEN,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val idleState = backStack.saveInstanceState()
        val expectedState = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        assertEquals(idleState, expectedState)
    }

    @Test
    fun `returns true when element on screen`() {

        val initialElement = Routing1
        val storedElements = listOf<BackStackElement<Routing>>(
            backStackElement(
                element = Routing4("Content"),
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val key: RoutingKey<Routing> = backStackKey(
            element = Routing4("Content"),
            uuid = 4
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
                uuid = 4,
                fromState = ON_SCREEN,
                targetState = ON_SCREEN
            ),
            backStackElement(
                element = Routing2,
                uuid = 2,
                fromState = STASHED_IN_BACK_STACK,
                targetState = STASHED_IN_BACK_STACK
            )
        )
        val savedStateMap = mutableMapOf<String, Any>().apply {
            this[Node.KEY_ROUTING_SOURCE] = storedElements
        }
        val backStack = BackStack<Routing>(
            initialElement = initialElement,
            savedStateMap = savedStateMap
        )

        val key: RoutingKey<Routing> = backStackKey(
            element = Routing2,
            uuid = 2
        )
        val isOnScreen = backStack.isOnScreen(key = key)

        assertEquals(isOnScreen, false)
    }

    private class DummyClearOperation(
        private val isApplicable: Boolean
    ) : Operation<Routing> {

        override fun isApplicable(elements: BackStackElements<Routing>): Boolean = isApplicable

        override fun invoke(
            elements: BackStackElements<Routing>,
            uuidGenerator: UuidGenerator
        ): BackStackElements<Routing> = emptyList()
    }
}
