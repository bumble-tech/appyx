package com.bumble.appyx.core.lifecycle

import android.os.Parcelable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.lifecycle.ChildLifecycleTest.TestNavModel.OnScreenState
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

class ChildLifecycleTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // region Tests

    @Test
    fun `on screen child follows parent state`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.testNavModel.add(key = "0", onScreen = true)

        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        assertEquals(
            Lifecycle.State.RESUMED,
            parent.findChild()?.lifecycle?.currentState,
        )
    }

    @Test
    fun `parent lifecycle callbacks is invoked before child`() {
        var counter = 1

        class TestResumedObserver : DefaultLifecycleObserver {
            var isInvoked = 0
            override fun onResume(owner: LifecycleOwner) {
                isInvoked = counter++
            }
        }

        val parentObserver = TestResumedObserver()
        val childObserver = TestResumedObserver()

        val parent = Parent(BuildContext.root(null)).build()
        parent.lifecycle.addObserver(parentObserver)
        parent.testNavModel.add(key = "0", onScreen = true)
        parent.updateLifecycleState(Lifecycle.State.STARTED)
        parent.findChild()?.lifecycle?.addObserver(childObserver)

        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        assertEquals(parentObserver.isInvoked, 1)
        assertEquals(childObserver.isInvoked, 2)
    }

    @Test
    fun `off screen child is limited to created`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.testNavModel.add(key = "0", onScreen = false)

        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        assertEquals(
            Lifecycle.State.CREATED,
            parent.findChild()?.lifecycle?.currentState,
        )
    }

    @Test
    fun `child is destroyed when is not represented in navModel anymore`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.testNavModel.add(key = "0", onScreen = true)
        parent.updateLifecycleState(Lifecycle.State.RESUMED)
        val child = parent.children.value.values.first().nodeOrNull

        parent.testNavModel.remove(key = "0")

        assertEquals(
            Lifecycle.State.DESTROYED,
            child?.lifecycle?.currentState,
        )
    }

    @Test
    fun `child is correctly moved from off screen to on screen`() {
        val parent = Parent(BuildContext.root(null)).build()
        parent.testNavModel.add(key = "0", onScreen = false)
        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        parent.testNavModel.changeState(key = "0", onScreen = true)

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
        parent.testNavModel.add(key = "0", onScreen = true)
        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        parent.testNavModel.changeState(key = "0", onScreen = false)

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
        parent.testNavModel.add(key = "0", onScreen = true)
        parent.updateLifecycleState(Lifecycle.State.RESUMED)

        parent.updateLifecycleState(Lifecycle.State.DESTROYED)

        assertEquals(
            Lifecycle.State.DESTROYED,
            parent.findChild()?.lifecycle?.currentState,
        )
    }

    // endregion

    // region Setup

    private class TestNavModel : BaseNavModel<StringNavTarget, OnScreenState>(
        screenResolver = object : OnScreenStateResolver<OnScreenState> {
            override fun isOnScreen(state: OnScreenState): Boolean = state.value
        },
        finalState = null,
        savedStateMap = null,
    ) {
        @Parcelize
        data class OnScreenState(val value: Boolean) : Parcelable

        override val initialElements: NavElements<StringNavTarget, OnScreenState> = emptyList()

        fun add(key: String, onScreen: Boolean) {
            updateState { list ->
                list + NavElement(
                    key = NavKey(StringNavTarget(key)),
                    targetState = OnScreenState(onScreen),
                    fromState = OnScreenState(onScreen),
                    operation = Operation.Noop(),
                )
            }
        }

        fun remove(key: String) {
            val stringNavTarget = StringNavTarget(key)
            updateState { list -> list.filter { it.key.navTarget != stringNavTarget } }
        }

        fun changeState(key: String, onScreen: Boolean) {
            val stringNavTarget = StringNavTarget(key)
            val onScreenState = OnScreenState(onScreen)
            updateState { list ->
                list
                    .map {
                        if (it.key.navTarget == stringNavTarget) {
                            it
                                .transitionTo(
                                    newTargetState = onScreenState,
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
        val testNavModel: TestNavModel = TestNavModel(),
    ) : ParentNode<StringNavTarget>(
        buildContext = buildContext,
        navModel = testNavModel,
    ) {
        override fun resolve(navTarget: StringNavTarget, buildContext: BuildContext): Node =
            Child(buildContext)

        @Composable
        override fun View(modifier: Modifier) {
            // no-op
        }

        fun findChild(): Child? =
            children.value.values.first().nodeOrNull as Child?

    }

    private class Child(buildContext: BuildContext) : Node(buildContext) {
        val lifecycleHistory = ArrayList<Lifecycle.State>()

        @Composable
        override fun View(modifier: Modifier) {
            // no-op
        }

        override fun updateLifecycleState(state: Lifecycle.State) {
            if (lifecycleHistory.lastOrNull() != state) {
                lifecycleHistory.add(state)
            }
            super.updateLifecycleState(state)
        }
    }

    @Parcelize
    data class StringNavTarget(val value: String) : Parcelable

    // endregion

}
