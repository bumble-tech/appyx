package com.bumble.appyx.utils.testing.unit.common.helper

import com.bumble.appyx.navigation.lifecycle.CommonLifecycle
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.build

fun <N : Node> N.nodeTestHelper() = NodeTestHelper(this)

open class NodeTestHelper<N : Node>(private val node: N) {

    private val nodeLifecycle
        get() = node.lifecycle

    init {
        node.build()
    }

    fun moveTo(state: CommonLifecycle.State) {
        require(state != CommonLifecycle.State.INITIALIZED) {
            "Can't move to INITIALIZED state"
        }
        node.updateLifecycleState(state)
    }

    /**
     * moves the Node to the desired state and then returns to the original state if possible
     */
    fun moveToStateAndCheck(state: CommonLifecycle.State, block: (N) -> Unit) {
        require(state != CommonLifecycle.State.INITIALIZED) { "Can't move to INITIALIZED state" }

        val returnTo =
            when (val current = nodeLifecycle.currentState) {
                CommonLifecycle.State.DESTROYED -> error("Can't move from DESTROYED state")
                CommonLifecycle.State.INITIALIZED -> CommonLifecycle.State.DESTROYED
                CommonLifecycle.State.CREATED,
                CommonLifecycle.State.STARTED,
                CommonLifecycle.State.RESUMED -> current
            }

        moveTo(state)
        try {
            block(node)
        } finally {
            moveTo(returnTo)
        }
    }
}
