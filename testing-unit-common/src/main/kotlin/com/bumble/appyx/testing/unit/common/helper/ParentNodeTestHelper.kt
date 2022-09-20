package com.bumble.appyx.testing.unit.common.helper

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.node.ParentNode
import kotlin.test.assertEquals
import kotlin.test.assertNull

fun <NavTarget : Any, N : ParentNode<NavTarget>> N.parentNodeTestHelper() =
    ParentNodeTestHelper(this)

class ParentNodeTestHelper<NavTarget : Any, N : ParentNode<NavTarget>>(
    private val node: N
) : NodeTestHelper<N>(
    node = node
) {

    fun <NavTarget : Any> assertChildHasLifecycle(navTarget: NavTarget, state: Lifecycle.State) {
        val childMap = node.children.value
        val key = childMap.keys.find { it.navTarget == navTarget }

        if (key != null) {
            childMap.getValue(key).nodeOrNull.also { childNode ->
                if (childNode != null) {
                    assertEquals(
                        state,
                        childNode.lifecycle.currentState
                    )
                } else {
                    throw NullPointerException("Child node was not attached for navTarget $navTarget")
                }
            }
        } else {
            throw NullPointerException("No child for navTarget $navTarget")
        }
    }

    fun <NavTarget : Any> assertHasNoChild(navTarget: NavTarget) {
        val key = node.children.value.keys.find { it.navTarget == navTarget }
        assertNull(key)
    }
}
