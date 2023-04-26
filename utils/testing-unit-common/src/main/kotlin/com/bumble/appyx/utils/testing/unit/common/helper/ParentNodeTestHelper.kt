package com.bumble.appyx.utils.testing.unit.common.helper

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.navigation.children.nodeOrNull
import com.bumble.appyx.navigation.node.ParentNode
import kotlin.test.assertEquals
import kotlin.test.assertNull

fun <InteractionTarget : Any, N : ParentNode<InteractionTarget>> N.parentNodeTestHelper() =
    ParentNodeTestHelper(this)

class ParentNodeTestHelper<InteractionTarget : Any, N : ParentNode<InteractionTarget>>(
    private val node: N
) : NodeTestHelper<N>(
    node = node
) {

    fun <InteractionTarget : Any> assertChildHasLifecycle(
        interactionTarget: InteractionTarget,
        state: Lifecycle.State
    ) {
        val childMap = node.children.value
        val key = childMap.keys.find { it.interactionTarget == interactionTarget }

        if (key != null) {
            childMap.getValue(key).nodeOrNull.also { childNode ->
                if (childNode != null) {
                    assertEquals(
                        state,
                        childNode.lifecycle.currentState
                    )
                } else {
                    throw NullPointerException("Child node was not attached for interactionTarget $interactionTarget")
                }
            }
        } else {
            throw NullPointerException("No child for interactionTarget $interactionTarget")
        }
    }

    fun <InteractionTarget : Any> assertHasNoChild(interactionTarget: InteractionTarget) {
        val key = node.children.value.keys.find { it.interactionTarget == interactionTarget }
        assertNull(key)
    }
}
