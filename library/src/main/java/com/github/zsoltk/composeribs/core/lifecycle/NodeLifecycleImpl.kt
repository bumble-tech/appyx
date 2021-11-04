package com.github.zsoltk.composeribs.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class NodeLifecycleImpl(owner: LifecycleOwner) : NodeLifecycle {

    private val lifecycleRegistry = LifecycleRegistry(owner)

    init {
        updateLifecycleState(Lifecycle.State.CREATED)
    }

    override fun getLifecycle(): Lifecycle =
        lifecycleRegistry

    override fun updateLifecycleState(state: Lifecycle.State) {
        if (lifecycleRegistry.currentState != state) {
            lifecycleRegistry.currentState = state
        }
    }

}
