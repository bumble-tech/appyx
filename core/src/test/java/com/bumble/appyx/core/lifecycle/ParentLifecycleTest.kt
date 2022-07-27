package com.bumble.appyx.core.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.lifecycle.ParentLifecycleTest.RoutingImpl.State
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// TODO: Make it BaseRoutingSource test
class ParentLifecycleTest {

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

    private class RoutingImpl : BaseRoutingSource<String, State>(
        screenResolver = object : OnScreenStateResolver<State> {
            override fun isOnScreen(state: State): Boolean =
                when (state) {
                    State.StateOne,
                    State.StateTwo,
                    State.StateThree -> false
                    State.StateFour -> true
                }
        },
        finalStates = emptySet(),
        savedStateMap = null,
    ) {

        enum class State {
            StateOne,
            StateTwo,
            StateThree,
            StateFour,
        }

        override val initialElements: RoutingElements<String, State> = emptyList()

        override val canHandleBackPress: StateFlow<Boolean> =
            MutableStateFlow(false)

        override fun onBackPressed() {
            // no-op
        }

        fun add(routing: String, defaultState: State) {
            updateState { list ->
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
                value = elements.value.find { it.key.routing == routing },
                lazyMessage = { "element with routing $routing is not found" },
            )
        }

        fun remove(routing: String) {
            updateState { list -> list.filter { it.key.routing != routing } }
        }

        fun changeState(routing: String, defaultState: State) {
            updateState { list ->
                list
                    .map {
                        if (it.key.routing == routing) {
                            it.transitionTo(
                                newTargetState = defaultState,
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

}
