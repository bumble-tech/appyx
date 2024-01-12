package com.bumble.appyx.utils.testing.unit.common.util

import com.bumble.appyx.navigation.builder.SimpleBuilder
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.AbstractNode
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InteropSimpleBuilderStub(
    val delegate: (NodeContext) -> AbstractNode = { _ -> NodeStub() },
) : SimpleBuilder() {

    var lastNode: AbstractNode? = null
        private set

    override fun build(nodeContext: NodeContext): AbstractNode =
        delegate(nodeContext).also {
            lastNode = it
        }

    fun assertCreatedNode() {
        assertTrue(lastNode != null, "Has not created any node")
    }

    fun assertLastNodeState(state: Lifecycle.State) {
        assertCreatedNode()
        assertEquals(state, lastNode!!.lifecycle.currentState)
    }
}
