package com.github.zsoltk.composeribs.core.node

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.plugin.NodeLifecycleAware
import com.github.zsoltk.composeribs.core.testutils.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class NodeTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private var lifecycle: Lifecycle = object : Lifecycle() {
        override fun addObserver(observer: LifecycleObserver) {}

        override fun removeObserver(observer: LifecycleObserver) {}

        override fun getCurrentState() = State.DESTROYED
    }

    private val nodeLifecycleAwarePlugin = object : NodeLifecycleAware {
        override fun onCreate(lifecycle: Lifecycle) {
            this@NodeTest.lifecycle = lifecycle
        }
    }

    @Test
    fun `when node built then node aware plugin receives node lifecycle`() {
        val parent = TestNode(BuildContext.root(null)).build()

        assertEquals(parent.lifecycle, lifecycle)
    }


    @Test
    fun `when node is not built then node aware plugin does not receive node lifecycle`() {
        val parent = TestNode(BuildContext.root(null))

        assertNotEquals(parent.lifecycle, lifecycle)
    }

    private inner class TestNode(buildContext: BuildContext) : Node(
        buildContext = buildContext,
        plugins = listOf(nodeLifecycleAwarePlugin)
    ) {
        @Composable
        override fun View() {
        }
    }


}
