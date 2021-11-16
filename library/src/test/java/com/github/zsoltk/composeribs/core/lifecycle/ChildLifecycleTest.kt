package com.github.zsoltk.composeribs.core.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.children.ChildEntry
import com.github.zsoltk.composeribs.core.children.nodeOrNull
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.testutils.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
        val parent = Parent(BuildContext.root(null))
        parent.routing.add(key = "0", onScreen = true)

        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        assertEquals(
            Lifecycle.State.RESUMED,
            parent.children.value.values.first().nodeOrNull?.lifecycle?.currentState,
        )
    }

    @Test
    fun `off screen child is limited to created`() {
        val parent = Parent(BuildContext.root(null))
        parent.routing.add(key = "0", onScreen = false)

        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        assertEquals(
            Lifecycle.State.CREATED,
            parent.children.value.values.first().nodeOrNull?.lifecycle?.currentState,
        )
    }

    @Test
    fun `child is destroyed when is not represented in routing source anymore`() {
        val parent = Parent(BuildContext.root(null))
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
        val parent = Parent(BuildContext.root(null))
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
        val parent = Parent(BuildContext.root(null))
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

        override val all: StateFlow<List<RoutingElement<String, Boolean>>> =
            state

        override val onScreen: StateFlow<List<RoutingElement<String, Boolean>>> =
            state
                .map { it.filter { it.targetState } }
                .stateIn(scope, SharingStarted.Eagerly, emptyList())

        override val offScreen: StateFlow<List<RoutingElement<String, Boolean>>> =
            state
                .map { it.filter { !it.targetState } }
                .stateIn(scope, SharingStarted.Eagerly, emptyList())

        override val canHandleBackPress: StateFlow<Boolean> =
            MutableStateFlow(false)

        override fun onBackPressed() {
            // no-op
        }

        override fun onTransitionFinished(key: RoutingKey<String>) {
            // no-op
        }

        fun add(key: String, onScreen: Boolean) {
            state.update { list ->
                list + RoutingElement(
                    RoutingKey(key),
                    onScreen,
                    onScreen,
                    Operation.Noop()
                )
            }
        }

        fun remove(key: String) {
            state.update { list -> list.filter { it.key.routing != key } }
        }

        fun changeState(key: String, onScreen: Boolean) {
            state.update { list ->
                list.map {
                    if (it.key.routing == key) {
                        it.copy(fromState = onScreen, targetState = onScreen)
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
        override fun View() {
        }
    }

    private class Child(
        val id: String,
        buildContext: BuildContext
    ) : Node(buildContext) {
        @Composable
        override fun View() {
        }
    }

    // endregion

}
