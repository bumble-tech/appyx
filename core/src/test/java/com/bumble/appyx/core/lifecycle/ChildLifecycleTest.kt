package com.bumble.appyx.core.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.routing.BaseRoutingSource
import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.RoutingElement
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.onscreen.OnScreenStateResolver
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ChildLifecycleTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // region Tests

    @Test
    fun `on screen child follows parent state`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.routing.add(key = "0", onScreen = true)

        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        assertEquals(
            Lifecycle.State.RESUMED,
            parent.findChild()?.lifecycle?.currentState,
        )
    }

    @Test
    fun `off screen child is limited to created`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.routing.add(key = "0", onScreen = false)

        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        assertEquals(
            Lifecycle.State.CREATED,
            parent.findChild()?.lifecycle?.currentState,
        )
    }

    @Test
    fun `child is destroyed when is not represented in routing source anymore`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.routing.add(key = "0", onScreen = true)
        parent.updateLifecycleState(Lifecycle.State.RESUMED)
        val child = parent.children.value.values.first().nodeOrNull

        parent.routing.remove(key = "0")

        assertEquals(
            Lifecycle.State.DESTROYED,
            child?.lifecycle?.currentState,
        )
    }

    @Test
    fun `child is correctly moved from off screen to on screen`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.routing.add(key = "0", onScreen = false)
        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        parent.routing.changeState(key = "0", onScreen = true)

        assertEquals(
            Lifecycle.State.RESUMED,
            parent.findChild()?.lifecycle?.currentState,
        )
        assertEquals(
            listOf(Lifecycle.State.CREATED, Lifecycle.State.RESUMED),
            parent.findChild()?.lifecycleHistory,
        )
    }

    @Test
    fun `child is correctly moved from on screen to off screen`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.routing.add(key = "0", onScreen = true)
        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        parent.routing.changeState(key = "0", onScreen = false)

        assertEquals(
            Lifecycle.State.CREATED,
            parent.findChild()?.lifecycle?.currentState,
        )
        assertEquals(
            listOf(Lifecycle.State.CREATED, Lifecycle.State.RESUMED, Lifecycle.State.CREATED),
            parent.findChild()?.lifecycleHistory,
        )
    }

    @Test
    fun `child is destroyed when parent is destroyed`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.routing.add(key = "0", onScreen = true)
        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        parent.updateLifecycleState(Lifecycle.State.DESTROYED)

        assertEquals(
            Lifecycle.State.DESTROYED,
            parent.findChild()?.lifecycle?.currentState,
        )
    }

    // endregion

    // region Setup

    private class RoutingImpl : BaseRoutingSource<String, Boolean>(
        screenResolver = object : OnScreenStateResolver<Boolean> {
            override fun isOnScreen(state: Boolean): Boolean = state
        },
        finalState = null,
        savedStateMap = null,
    ) {
        override val initialElements: RoutingElements<String, Boolean> = emptyList()

        fun add(key: String, onScreen: Boolean) {
            updateState { list ->
                list + RoutingElement(
                    key = RoutingKey(key),
                    targetState = onScreen,
                    fromState = onScreen,
                    operation = Operation.Noop(),
                )
            }
        }

        fun remove(key: String) {
            updateState { list -> list.filter { it.key.routing != key } }
        }

        fun changeState(key: String, onScreen: Boolean) {
            updateState { list ->
                list
                    .map {
                        if (it.key.routing == key) {
                            it
                                .transitionTo(
                                    newTargetState = onScreen,
                                    operation = Operation.Noop()
                                )
                                .onTransitionFinished()
                        } else {
                            it
                        }
                    }
            }
        }
    }

    private class Parent(
        buildContext: BuildContext,
        val routing: RoutingImpl = RoutingImpl(),
    ) : ParentNode<String>(
        buildContext = buildContext,
        routingSource = routing,
    ) {
        override fun resolve(routing: String, buildContext: BuildContext): Node =
            Child(routing, buildContext)

        @Composable
        override fun View(modifier: Modifier) {
        }

        fun findChild(): Child? =
            children.value.values.first().nodeOrNull as Child?

    }

    private class Child(
        val id: String,
        buildContext: BuildContext
    ) : Node(buildContext) {
        val lifecycleHistory = ArrayList<Lifecycle.State>()

        @Composable
        override fun View(modifier: Modifier) {
        }

        override fun updateLifecycleState(state: Lifecycle.State) {
            if (lifecycleHistory.lastOrNull() != state) {
                lifecycleHistory.add(state)
            }
            super.updateLifecycleState(state)
        }
    }

    // endregion

}
