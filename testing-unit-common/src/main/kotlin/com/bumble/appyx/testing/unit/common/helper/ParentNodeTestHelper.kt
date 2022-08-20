package com.bumble.appyx.testing.unit.common.helper

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.node.ParentNode
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
            childMap.getValue(key).nodeOrNull.also { childNode ->
                if (childNode != null) {
                    assertEquals(
                        state,
                        childNode.lifecycle.currentState
                    )
                } else {
                    throw NullPointerException("Child node was not attached for routing $routing")
                }
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
