package com.bumble.appyx.testing.unit.common.util

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.builder.SimpleBuilder
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InteropSimpleBuilderStub(
    val delegate: (BuildContext) -> Node = { _ -> NodeStub() },
) : SimpleBuilder() {

    private val _createdNodes = ArrayList<Node>()

    val last: Node?
        get() = _createdNodes.lastOrNull()

    override fun build(buildContext: BuildContext): Node =
        delegate(buildContext).also {
            _createdNodes += it
        }

    fun assertCreatedNode() {
        assertTrue(last != null, "Has not created any node")
    }

    fun assertLastNodeState(state: Lifecycle.State) {
        assertEquals(state, last?.lifecycle?.currentState)
    }
}
