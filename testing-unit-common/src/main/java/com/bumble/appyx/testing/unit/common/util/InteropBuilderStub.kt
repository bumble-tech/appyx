package com.bumble.appyx.testing.unit.common.util

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.builder.Builder
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InteropBuilderStub<P>(
    val delegate: (BuildContext, P) -> Node = { _, _ -> NodeStub() },
) : Builder<P>() {

    var lastNode: Node? = null
        private set

    var lastParam: P? = null
        private set

    override fun build(buildContext: BuildContext, payload: P): Node =
        delegate(buildContext, payload).also {
            lastNode = it
            lastParam = payload
        }

    fun assertLastParam(param: P) {
        assertEquals(param, lastParam)
    }

    fun assertLastParam(assert: P.() -> Boolean) {
        assertTrue(
            lastParam != null && assert(lastParam!!),
            "$lastParam does not satisfy requirements"
        )
    }

    fun assertCreatedNode() {
        assertTrue(lastNode != null, "Has not created any node")
    }

    fun assertLastNodeState(state: Lifecycle.State) {
        assertCreatedNode()
        assertEquals(state, lastNode?.lifecycle?.currentState)
    }
}
