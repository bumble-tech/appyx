package com.bumble.appyx.core.plugin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.testutils.MainDispatcherRule
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DestroyableTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `destroy is invoked when node is destroyed`() {
        var isDestroyed = false
        val stub = Destroyable { isDestroyed = true }

        val node = object : Node(BuildContext.root(null), plugins = listOf(stub)) {
            @Composable
            override fun View(modifier: Modifier) {
            }
        }
        node.build()
        node.updateLifecycleState(Lifecycle.State.RESUMED)
        node.updateLifecycleState(Lifecycle.State.DESTROYED)

        assertTrue("Destroyable is not destroyed", isDestroyed)
    }

}
