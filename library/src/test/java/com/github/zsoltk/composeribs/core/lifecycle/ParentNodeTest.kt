package com.github.zsoltk.composeribs.core.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.github.zsoltk.composeribs.core.children.ChildEntry
import com.github.zsoltk.composeribs.core.lifecycle.ParentNodeTest.RoutingImpl.State
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.node.build
import com.github.zsoltk.composeribs.core.routing.OnScreenMapper
import com.github.zsoltk.composeribs.core.routing.OnScreenStateResolver
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.testutils.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext

class ParentNodeTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `parent node finishes transitions for off screen elements when lifecycle is not stopped`() {
        val parent = Parent(BuildContext.root(null)).build()
        val routingSource = parent.routing
        val routing = "0"
        parent.updateLifecycleState(Lifecycle.State.STARTED)
        routingSource.add(routing = routing, defaultState = State.StateOne)

        routingSource.changeState(routing = routing, State.StateTwo)

        val element = routingSource.get(routing = routing)

        assertEquals(
            element.fromState,
            element.targetState
        )
    }

    private class RoutingImpl : RoutingSource<String, State> {

        enum class State {
            StateOne,
            StateTwo,
            StateThree,
            StateFour,
        }

        private val state = MutableStateFlow<List<RoutingElement<String, State>>>(emptyList())
        private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
        private val onScreenResolver = object : OnScreenStateResolver<State> {
            override fun isOnScreen(state: State): Boolean =
                when (state) {
                    State.StateOne,
                    State.StateTwo,
                    State.StateThree -> false
                    State.StateFour -> true
                }
        }
        private val onScreenMapper = OnScreenMapper<String, State>(scope, onScreenResolver)

        override val elements: StateFlow<List<RoutingElement<String, State>>> =
            state

        override val onScreen: StateFlow<RoutingElements<String, out State>> =
            onScreenMapper.resolveOnScreenElements(state)

        override val offScreen: StateFlow<RoutingElements<String, out State>> =
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

        fun add(routing: String, defaultState: State) {
            state.update { list ->
                list + RoutingElement(
                    key = RoutingKey(routing),
                    targetState = defaultState,
                    fromState = defaultState,
                    operation = Operation.Noop(),
                )
            }
        }

        fun get(routing: String): RoutingElement<String, State> {
            return requireNotNull(
                state.value.find { it.key.routing == routing },
                { "element with routing $routing is not found" }
            )
        }

        fun remove(routing: String) {
            state.update { list -> list.filter { it.key.routing != routing } }
        }

        fun changeState(routing: String, defaultState: State) {
            state.update { list ->
                list
                    .map {
                        if (it.key.routing == routing) {
                            it.transitionTo(
                                targetState = defaultState,
                                operation = Operation.Noop()
                            )
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

}
