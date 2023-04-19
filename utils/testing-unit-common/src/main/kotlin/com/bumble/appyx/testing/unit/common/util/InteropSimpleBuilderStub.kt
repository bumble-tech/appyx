package com.bumble.appyx.testing.unit.common.util

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.navigation.builder.SimpleBuilder
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InteropSimpleBuilderStub(
    val delegate: (BuildContext) -> Node = { _ -> NodeStub() },
) : SimpleBuilder() {

    var lastNode: Node? = null
        private set

    override fun build(buildContext: BuildContext): Node =
        delegate(buildContext).also {
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
