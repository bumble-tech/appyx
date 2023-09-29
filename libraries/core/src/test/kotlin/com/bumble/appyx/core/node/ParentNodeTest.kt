package com.bumble.appyx.core.node

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.navigation.model.permanent.operation.addUnique
import com.bumble.appyx.core.node.ParentNodeTest.NavTarget.ChildA
import com.bumble.appyx.core.node.ParentNodeTest.NavTarget.ChildB
import com.bumble.appyx.core.node.ParentNodeTest.NodeB.Companion.StatusExecuted
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ParentNodeTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Test
    fun `waitForChildAttached WHEN expected navTarget provided THEN returns associated Node`() =
        testScope.runTest {
            //given
            val backStack: BackStack<NavTarget> = buildBackStack()
            val node = buildParentNode(backStack)
            var status: String? = null
            launch {
                status = node.waitForBAttached().changeStatus().status
            }
            assertNull(status)

            //when
            backStack.push(ChildB)

            //then
            assertEquals(status, StatusExecuted)
        }

    @Test(expected = IllegalStateException::class)
    fun `attachWorkflow WHEN expected navTarget not provided THEN fails after timeout`() =
        testScope.runTest {
            //given
            val backStack: BackStack<NavTarget> = buildBackStack()
            val node = buildParentNode(backStack)

            //when
            node.incorrectlyAttachChildB()
        }

    @Test
    fun `attachWorkflow WHEN expected navTarget provided THEN returns expected Node`() =
        testScope.runTest {
            //given
            val backStack: BackStack<NavTarget> = buildBackStack()
            val node = buildParentNode(backStack)

            // when
            val attachedNode: Node = node.attachChildB()

            //then
            assertTrue(attachedNode is NodeB)
        }

    @Test
    fun `GIVEN node with PermanentNavModel WHEN saves state THEN restores state correctly`() =
        testScope.runTest {
            //given
            val permanentNavModel: PermanentNavModel<NavTarget> =
                PermanentNavModel(ChildA, savedStateMap = null)
            val node = buildParentNode(permanentNavModel = permanentNavModel)
            assertChildrenCount(node, 1)

            // when
            permanentNavModel.addUnique(ChildB)
            assertChildrenCount(node, 2)
            val state = node.saveInstanceState { true }
            val restoredStateNode =
                buildParentNode(permanentNavModel = permanentNavModel, savedStateMap = state)

            //then
            assertChildrenCount(restoredStateNode, 2)
        }

    private fun assertChildrenCount(node: ParentNode<*>, expectedCount: Int) {
        val childrenCount = node.children.value.values.mapNotNull { it.nodeOrNull }.count()
        assertTrue(childrenCount == expectedCount)
    }

    private fun buildBackStack(initialElement: NavTarget = ChildA) =
        BackStack(initialElement = initialElement, savedStateMap = null)

    private fun buildParentNode(backStack: BackStack<NavTarget>) =
        TestParentNode(backStack).apply { onBuilt() }

    private fun buildParentNode(
        savedStateMap: SavedStateMap? = null,
        permanentNavModel: PermanentNavModel<NavTarget>
    ) =
        TestPermanentModelParentNode(
            savedStateMap = savedStateMap,
            permanentNavModel = permanentNavModel
        ).apply { onBuilt() }

    private class TestPermanentModelParentNode(
        savedStateMap: SavedStateMap? = null,
        permanentNavModel: PermanentNavModel<NavTarget>
    ) : ParentNode<NavTarget>(
        buildContext = BuildContext.root(savedStateMap),
        navModel = permanentNavModel
    ) {

        override fun resolve(navTarget: NavTarget, buildContext: BuildContext) = when (navTarget) {
            ChildA -> node(buildContext) {}
            ChildB -> NodeB(buildContext)
        }
    }

    private class TestParentNode(
        private val backStack: BackStack<NavTarget>
    ) : ParentNode<NavTarget>(
        buildContext = BuildContext.root(null),
        navModel = backStack
    ) {

        suspend fun waitForBAttached(): NodeB {
            return waitForChildAttached()
        }

        suspend fun attachChildB(): NodeB {
            return attachChild {
                backStack.push(ChildB)
            }
        }

        suspend fun incorrectlyAttachChildB(): NodeB {
            return attachChild {
                backStack.push(ChildA)
            }
        }

        override fun resolve(navTarget: NavTarget, buildContext: BuildContext) = when (navTarget) {
            ChildA -> node(buildContext) {}
            ChildB -> NodeB(buildContext)
        }
    }

    private sealed class NavTarget {
        object ChildA : NavTarget()
        object ChildB : NavTarget()
    }

    private class NodeB(buildContext: BuildContext) : Node(buildContext) {

        var status: String? = null
            private set

        suspend fun changeStatus(): NodeB {
            return executeAction {
                status = StatusExecuted
            }
        }

        companion object {
            const val StatusExecuted = "executed"
        }

    }
}
