package com.bumble.appyx.core.node

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNodeTest.NodeB.Companion.StatusExecuted
import com.bumble.appyx.core.node.ParentNodeTest.TestParentNode.NavTarget
import com.bumble.appyx.core.node.ParentNodeTest.TestParentNode.NavTarget.ChildA
import com.bumble.appyx.core.node.ParentNodeTest.TestParentNode.NavTarget.ChildB
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import java.lang.IllegalStateException
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

    private fun buildBackStack(initialElement: NavTarget = ChildA) =
        BackStack(initialElement = initialElement, savedStateMap = null)

    private fun buildParentNode(backStack: BackStack<NavTarget>) =
        TestParentNode(backStack).apply { onBuilt() }

    private class TestParentNode(
        private val backStack: BackStack<NavTarget>
    ) : ParentNode<NavTarget>(
        buildContext = BuildContext.root(null),
        navModel = backStack
    ) {

        sealed class NavTarget {
            object ChildA : NavTarget()
            object ChildB : NavTarget()
        }

        suspend fun waitForBAttached(): NodeB {
            return waitForChildAttached()
        }

        suspend fun attachChildB(): NodeB {
            return attachWorkflow {
                backStack.push(ChildB)
            }
        }

        suspend fun incorrectlyAttachChildB(): NodeB {
            return attachWorkflow {
                backStack.push(ChildA)
            }
        }

        override fun resolve(navTarget: NavTarget, buildContext: BuildContext) = when (navTarget) {
            ChildA -> node(buildContext) {}
            ChildB -> NodeB(buildContext)
        }
    }

    private class NodeB(buildContext: BuildContext) : Node(buildContext) {

        var status: String? = null
            private set

        suspend fun changeStatus(): NodeB {
            return executeWorkflow {
                status = StatusExecuted
            }
        }

        companion object {
            const val StatusExecuted = "executed"
        }

    }
}
