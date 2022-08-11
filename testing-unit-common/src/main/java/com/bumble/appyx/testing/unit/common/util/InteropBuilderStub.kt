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

    private val _createdNodes = ArrayList<Node>()

    val last: Node?
        get() = _createdNodes.lastOrNull()

    var lastParam: P? = null
        private set

    override fun build(buildContext: BuildContext, payload: P): Node =
        delegate(buildContext, payload).also {
            _createdNodes += it
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
        assertTrue(last != null, "Has not created any node")
    }

    fun assertLastNodeState(state: Lifecycle.State) {
        assertEquals(state, last?.lifecycle?.currentState)
    }
}
