package com.bumble.appyx.core.node

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.NodeTest.TestNode.Companion.StatusExecuted
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NodeTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Test
    fun `executeWorkflow WHEN called THEN executes action returns current node`() =
        testScope.runTest {
            val node = TestNode()
            assertNull(node.status)

            node.changeStatus()

            assertEquals(node.status, StatusExecuted)
        }


    private class TestNode : Node(BuildContext.root(null)) {

        var status: String? = null
            private set

        suspend fun changeStatus(): TestNode {
            return executeWorkflow {
                status = StatusExecuted
            }
        }

        companion object {
            const val StatusExecuted = "executed"
        }

    }
}
