package com.bumble.appyx.core.plugin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SavesInstanceStateTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `saveInstanceState is invoked when node saves instance state`() {
        val stub = createPlugin()

        val node = createNode(stub)
        node.build()
        val state = node.saveInstanceState { true }

        assertTrue(state["key"] == "value")
    }

    @Test
    fun `saveInstanceState does not allow duplicate keys`() {
        val stub1 = createPlugin()
        val stub2 = createPlugin()

        val node = createNode(stub1, stub2)
        node.build()

        assertThrows(IllegalStateException::class.java) {
            node.saveInstanceState { true }
        }
    }

    private fun createPlugin() =
        object : SavesInstanceState {
            override fun saveInstanceState(state: MutableSavedStateMap) {
                state["key"] = "value"
            }
        }

    private fun createNode(vararg plugins: Plugin) =
        object : Node(BuildContext.root(null), plugins = plugins.toList()) {
            @Composable
            override fun View(modifier: Modifier) {
            }
        }

}
