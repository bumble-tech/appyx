package com.bumble.appyx.core.node

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNodeTest.NodeB.Companion.StatusExecuted
import com.bumble.appyx.core.node.ParentNodeTest.TestParentNode.Routing
import com.bumble.appyx.core.node.ParentNodeTest.TestParentNode.Routing.ChildA
import com.bumble.appyx.core.node.ParentNodeTest.TestParentNode.Routing.ChildB
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.operation.push
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import java.lang.IllegalStateException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsInstanceOf
import org.hamcrest.core.IsNull
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
    fun `waitForChildAttached WHEN expected routing provided THEN returns associated Node`() =
        testScope.runTest {
            //given
            val backStack: BackStack<Routing> = buildBackStack()
            val node = buildParentNode(backStack)
            var status: String? = null
            launch {
                status = node.waitForBAttached().changeStatus().status
            }
            assertThat(status, IsNull())

            //when
            backStack.push(ChildB)

            //then
            assertThat(status, IsEqual(StatusExecuted))
        }

    @Test(expected = IllegalStateException::class)
    fun `attachWorkflow WHEN expected routing not provided THEN fails after timeout`() =
        testScope.runTest {
            //given
            val backStack: BackStack<Routing> = buildBackStack()
            val node = buildParentNode(backStack)

            //when
            node.incorrectAttachChildB()
        }

    @Test
    fun `attachWorkflow WHEN expected routing provided THEN returns expected Node`() =
        testScope.runTest {
            //given
            val backStack: BackStack<Routing> = buildBackStack()
            val node = buildParentNode(backStack)

            // when
            val attachedNode: Node = node.attachChildB()

            //then
            assertThat(attachedNode, IsInstanceOf(NodeB::class.java))
        }

    private fun buildBackStack(initialElement: Routing = ChildA) =
        BackStack(initialElement = initialElement, savedStateMap = null)

    private fun buildParentNode(backStack: BackStack<Routing>) =
        TestParentNode(backStack).apply { onBuilt() }

    private class TestParentNode(
        private val backStack: BackStack<Routing>
    ) : ParentNode<Routing>(
        buildContext = BuildContext.root(null),
        routingSource = backStack
    ) {

        sealed class Routing {
            object ChildA : Routing()
            object ChildB : Routing()
        }

        suspend fun waitForBAttached(): NodeB {
            return waitForChildAttached()
        }

        suspend fun attachChildB(): NodeB {
            return attachWorkflow {
                backStack.push(ChildB)
            }
        }

        suspend fun incorrectAttachChildB(): NodeB {
            return attachWorkflow {
                backStack.push(ChildA)
            }
        }

        override fun resolve(routing: Routing, buildContext: BuildContext) = when (routing) {
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
