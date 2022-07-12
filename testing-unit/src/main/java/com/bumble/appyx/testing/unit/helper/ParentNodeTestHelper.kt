package com.bumble.appyx.testing.unit.helper

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

fun <Routing : Any> ParentNode<Routing>.parentNodeTestHelper() =
    ParentNodeTestHelper(this)

class ParentNodeTestHelper<Routing : Any>(private val node: ParentNode<Routing>) {

    private val nodeTestHelper = node.nodeTestHelper()

    fun moveTo(state: Lifecycle.State) {
        nodeTestHelper.moveTo(state)
    }

    /**
     * moves the Node to the desired state and then returns to the original state if possible
     */
    fun moveToStateAndCheck(
        state: Lifecycle.State,
        block: ParentNodeTestHelper<Routing>.(Node) -> Unit
    ) {
        val newBlock: (Node) -> Unit = { block(it) }
        nodeTestHelper.moveToStateAndCheck(state, newBlock)
    }

    fun <Routing : Any> assertChildHasLifecycle(routing: Routing, state: Lifecycle.State) {
        node.children.value.keys
            .find { it.routing == routing }
            ?.let { node.children.value[it]?.nodeOrNull }
            ?.also {
                assertEquals(
                    state,
                    node.lifecycle.currentState
                )
            }
    }

    fun <Routing : Any> assertHasNoChild(routing: Routing) {
        val key = node.children.value.keys.find { it.routing == routing }
        assertNull(key)
    }
}