package com.bumble.appyx.core.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.plugin.NodeLifecycleAware
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class NodeLifecycleAwareTest {

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
            this@NodeLifecycleAwareTest.lifecycle = lifecycle
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
        override fun View(modifier: Modifier) {
        }
    }


}
