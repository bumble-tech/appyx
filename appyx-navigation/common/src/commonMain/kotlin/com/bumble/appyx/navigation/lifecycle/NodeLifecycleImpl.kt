package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.platform.Lifecycle
import com.bumble.appyx.navigation.platform.LifecycleOwner
import com.bumble.appyx.navigation.platform.LifecycleRegistry

internal class NodeLifecycleImpl(owner: LifecycleOwner) : NodeLifecycle {

    private val lifecycleRegistry = LifecycleRegistry(owner)

    override val lifecycle: Lifecycle =
        lifecycleRegistry

    override fun updateLifecycleState(state: Lifecycle.State) {
        lifecycleRegistry.currentState = state
    }

}
