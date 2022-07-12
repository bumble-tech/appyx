package com.bumble.appyx.testing.unit.helper

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.node.ParentNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

fun <Routing : Any, N : ParentNode<Routing>> N.parentNodeTestHelper() =
    ParentNodeTestHelper(this)

class ParentNodeTestHelper<Routing : Any, N : ParentNode<Routing>>(
    private val node: N
) : NodeTestHelper<N>(
    node = node
) {

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
