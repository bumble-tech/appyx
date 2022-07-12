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
        val childMap = node.children.value
        val key = childMap.keys.find { it.routing == routing }

        if (key != null) {
            childMap[key]?.nodeOrNull?.also {
                assertEquals(
                    state,
                    node.lifecycle.currentState
                )
            }
        } else {
            throw NullPointerException("No child for routing $routing")
        }
    }

    fun <Routing : Any> assertHasNoChild(routing: Routing) {
        val key = node.children.value.keys.find { it.routing == routing }
        assertNull(key)
    }
}
