package com.bumble.appyx.core.lifecycle

import android.os.Parcelable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.lifecycle.ParentLifecycleTest.Parent.StringNavTarget
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.build
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import kotlinx.parcelize.Parcelize
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// TODO: Make it BaseNavModel test https://github.com/bumble-tech/appyx/issues/192
class ParentLifecycleTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `parent node finishes transitions for off screen elements when lifecycle is not stopped`() {
        val parent = Parent(BuildContext.root(null)).build()
        val navModel = parent.navModelImpl
        val navTarget = StringNavTarget("0")
        parent.updateLifecycleState(Lifecycle.State.STARTED)
        navModel.add(navTarget = navTarget, defaultState = NavModelImpl.State.StateOne)

        navModel.changeState(navTarget = navTarget, NavModelImpl.State.StateTwo)

        val element = navModel.get(navTarget = navTarget)

        assertEquals(
            element.fromState,
            element.targetState
        )
    }

    private class NavModelImpl : BaseNavModel<StringNavTarget, NavModelImpl.State>(
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

        @Parcelize
        enum class State: Parcelable {
            StateOne,
            StateTwo,
            StateThree,
            StateFour,
        }

        override val initialElements: NavElements<StringNavTarget, State> = emptyList()

        fun add(navTarget: StringNavTarget, defaultState: State) {
            updateState { list ->
                list + NavElement(
                    key = NavKey(navTarget),
                    targetState = defaultState,
                    fromState = defaultState,
                    operation = Operation.Noop(),
                )
            }
        }

        fun get(navTarget: StringNavTarget): NavElement<StringNavTarget, State> {
            return requireNotNull(
                value = elements.value.find { it.key.navTarget == navTarget },
                lazyMessage = { "element with navTarget $navTarget is not found" },
            )
        }

        fun remove(navTarget: StringNavTarget) {
            updateState { list -> list.filter { it.key.navTarget != navTarget } }
        }

        fun changeState(navTarget: StringNavTarget, defaultState: State) {
            updateState { list ->
                list
                    .map {
                        if (it.key.navTarget == navTarget) {
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
        val navModelImpl: NavModelImpl = NavModelImpl(),
    ) : ParentNode<StringNavTarget>(
        buildContext = buildContext,
        navModel = navModelImpl,
    ) {
        @Parcelize
        data class StringNavTarget(val value: String) : Parcelable

        override fun resolve(navTarget: StringNavTarget, buildContext: BuildContext): Node =
            Child(buildContext)

        @Composable
        override fun View(modifier: Modifier) {
            // no-op
        }
    }

    private class Child(buildContext: BuildContext) : Node(buildContext) {
        @Composable
        override fun View(modifier: Modifier) {
            // no-op
        }
    }

}
