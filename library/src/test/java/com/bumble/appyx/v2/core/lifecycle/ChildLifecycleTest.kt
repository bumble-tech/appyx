package com.bumble.appyx.v2.core.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.v2.core.children.ChildEntry
import com.bumble.appyx.v2.core.children.nodeOrNull
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.node.build
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenMapper
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenStateResolver
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingElement
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.RoutingSource
import com.bumble.appyx.v2.core.testutils.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext

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
            parent.children.value.values.first().nodeOrNull?.lifecycle?.currentState,
        )
    }

    @Test
    fun `off screen child is limited to created`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.routing.add(key = "0", onScreen = false)

        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        assertEquals(
            Lifecycle.State.CREATED,
            parent.children.value.values.first().nodeOrNull?.lifecycle?.currentState,
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
            parent.children.value.values.first().nodeOrNull?.lifecycle?.currentState,
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
            parent.children.value.values.first().nodeOrNull?.lifecycle?.currentState,
        )
    }

    // endregion

    // region Setup

    private class RoutingImpl : RoutingSource<String, Boolean> {

        private val state = MutableStateFlow<List<RoutingElement<String, Boolean>>>(emptyList())
        private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
        private val onScreenResolver = object : OnScreenStateResolver<Boolean> {
            override fun isOnScreen(state: Boolean): Boolean = state
        }
        private val onScreenMapper = OnScreenMapper<String, Boolean>(scope, onScreenResolver)

        override val elements: StateFlow<List<RoutingElement<String, Boolean>>> =
            state

        override val onScreen: StateFlow<RoutingElements<String, out Boolean>> =
            onScreenMapper.resolveOnScreenElements(state)

        override val offScreen: StateFlow<RoutingElements<String, out Boolean>> =
            onScreenMapper.resolveOffScreenElements(state)

        override val canHandleBackPress: StateFlow<Boolean> =
            MutableStateFlow(false)

        override fun onBackPressed() {
            // no-op
        }

        override fun onTransitionFinished(key: RoutingKey<String>) {
            state.update { list ->
                list.map {
                    if (it.key == key) {
                        it.onTransitionFinished()
                    } else {
                        it
                    }
                }
            }
        }

        fun add(key: String, onScreen: Boolean) {
            state.update { list ->
                list + RoutingElement(
                    key = RoutingKey(key),
                    targetState = onScreen,
                    fromState = onScreen,
                    operation = Operation.Noop(),
                )
            }
        }

        fun remove(key: String) {
            state.update { list -> list.filter { it.key.routing != key } }
        }

        fun changeState(key: String, onScreen: Boolean) {
            state.update { list ->
                list
                    .map {
                        if (it.key.routing == key) {
                            it
                                .transitionTo(targetState = onScreen, operation = Operation.Noop())
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
        childMode = ChildEntry.ChildMode.EAGER,
    ) {
        override fun resolve(routing: String, buildContext: BuildContext): Node =
            Child(routing, buildContext)

        @Composable
        override fun View(modifier: Modifier) {
        }
    }

    private class Child(
        val id: String,
        buildContext: BuildContext
    ) : Node(buildContext) {
        @Composable
        override fun View(modifier: Modifier) {
        }
    }

    // endregion

}
