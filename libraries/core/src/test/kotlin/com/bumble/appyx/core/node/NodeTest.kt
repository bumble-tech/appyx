package com.bumble.appyx.core.node

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.NodeTest.TestNode.Companion.StatusExecuted
import com.bumble.appyx.core.store.RetainedInstanceStore
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.bumble.appyx.testing.unit.common.util.TestIntegrationPoint
import com.bumble.appyx.testing.unit.common.util.TestUpNavigationHandler
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
    private val retainedInstanceStore: RetainedInstanceStoreStub = RetainedInstanceStoreStub()

    @Test
    fun `executeWorkflow WHEN called THEN executes action returns current node`() =
        testScope.runTest {
            val node = TestNode(retainedInstanceStore)
            assertNull(node.status)

            node.changeStatus()

            assertEquals(node.status, StatusExecuted)
        }

    @Test
    fun `When node is destroyed AND not changing configurations THEN RetainedInstanceStore cleared`() =
        testScope.runTest {
            val node = TestNode(retainedInstanceStore)
            node.integrationPoint = TestIntegrationPoint(
                upNavigationHandler = TestUpNavigationHandler(),
                isChangingConfigurations = false
            )
            node.onBuilt()
            node.updateLifecycleState(Lifecycle.State.CREATED)

            node.updateLifecycleState(Lifecycle.State.DESTROYED)

            assertEquals(node.id, retainedInstanceStore.clearStoreId)
        }

    @Test
    fun `When node is destroyed AND changing configurations THEN RetainedInstanceStore not cleared`() =
        testScope.runTest {
            val node = TestNode(retainedInstanceStore)
            node.integrationPoint = TestIntegrationPoint(
                upNavigationHandler = TestUpNavigationHandler(),
                isChangingConfigurations = true
            )
            node.onBuilt()
            node.updateLifecycleState(Lifecycle.State.CREATED)

            node.updateLifecycleState(Lifecycle.State.DESTROYED)

            assertEquals(null, retainedInstanceStore.clearStoreId)
        }

    private class TestNode(retainedInstanceStore: RetainedInstanceStore) : Node(
        buildContext = BuildContext.root(null),
        retainedInstanceStore = retainedInstanceStore,
    ) {

        var status: String? = null
            private set

        suspend fun changeStatus(): TestNode {
            return executeAction {
                status = StatusExecuted
            }
        }

        companion object {
            const val StatusExecuted = "executed"
        }

    }

    private class RetainedInstanceStoreStub : RetainedInstanceStore {
        var clearStoreId: String? = null

        override fun <T : Any> get(storeId: String, key: String, disposer: (T) -> Unit, factory: () -> T): T {
            error("Not needed for this test")
        }

        override fun isRetainedByStoreId(storeId: String, value: Any): Boolean {
            error("Not needed for this test")
        }

        override fun isRetained(value: Any): Boolean {
            error("Not needed for this test")
        }

        override fun clearStore(storeId: String) {
            clearStoreId = storeId
        }
    }
}
