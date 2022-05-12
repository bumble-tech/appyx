package com.bumble.appyx.v2.core.plugin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.build
import com.bumble.appyx.v2.core.state.SavedStateWriter
import com.bumble.appyx.v2.core.testutils.MainDispatcherRule
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
        val stub = object : SavesInstanceState {
            override fun saveInstanceState(writer: SavedStateWriter) {
                writer.save("key", "value", this)
            }
        }

        val node = createNode(stub)
        node.build()
        val state = node.saveInstanceState { true }

        assertTrue(state["key"] == "value")
    }

    @Test
    fun `saveInstanceState does not allow duplicate keys`() {
        val stub1 = object : SavesInstanceState {
            override fun saveInstanceState(writer: SavedStateWriter) {
                writer.save("key", "value", this)
            }
        }
        val stub2 = object : SavesInstanceState {
            override fun saveInstanceState(writer: SavedStateWriter) {
                writer.save("key", "value", this)
            }
        }

        val node = createNode(stub1, stub2)
        node.build()

        assertThrows(IllegalStateException::class.java) {
            node.saveInstanceState { true }
        }
    }

    private fun createNode(vararg plugins: Plugin) =
        object : Node(BuildContext.root(null), plugins.toList()) {
            @Composable
            override fun View(modifier: Modifier) {
            }
        }

}
