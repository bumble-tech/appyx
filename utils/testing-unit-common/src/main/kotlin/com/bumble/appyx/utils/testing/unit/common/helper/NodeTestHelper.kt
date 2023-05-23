package com.bumble.appyx.utils.testing.unit.common.helper

import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.build
import com.bumble.appyx.navigation.platform.PlatformLifecycle

fun <N : Node> N.nodeTestHelper() = NodeTestHelper(this)

open class NodeTestHelper<N : Node>(private val node: N) {

    private val nodeLifecycle
        get() = node.lifecycle

    init {
        node.build()
    }

    fun moveTo(state: PlatformLifecycle.State) {
        require(state != PlatformLifecycle.State.INITIALIZED) {
            "Can't move to INITIALIZED state"
        }
        node.updateLifecycleState(state)
    }

    /**
     * moves the Node to the desired state and then returns to the original state if possible
     */
    fun moveToStateAndCheck(state: PlatformLifecycle.State, block: (N) -> Unit) {
        require(state != PlatformLifecycle.State.INITIALIZED) { "Can't move to INITIALIZED state" }

        val returnTo =
            when (val current = nodeLifecycle.currentState) {
                PlatformLifecycle.State.DESTROYED -> error("Can't move from DESTROYED state")
                PlatformLifecycle.State.INITIALIZED -> PlatformLifecycle.State.DESTROYED
                PlatformLifecycle.State.CREATED,
                PlatformLifecycle.State.STARTED,
                PlatformLifecycle.State.RESUMED -> current
            }

        moveTo(state)
        try {
            block(node)
        } finally {
            moveTo(returnTo)
        }
    }
}
