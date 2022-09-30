package com.bumble.appyx.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

internal class NodeLifecycleImpl(owner: LifecycleOwner) : NodeLifecycle {

    private var lock: Any? = null
    private val lifecycleRegistry = LifecycleRegistry(owner)

    override fun getLifecycle(): Lifecycle =
        lifecycleRegistry

    override fun lockCaller(caller: Any?) {
        lock = caller
    }

    override fun updateLifecycleState(state: Lifecycle.State, caller: Any?) {
        if (lock != null && lock != caller) return // Invalid caller, ignore
        lifecycleRegistry.currentState = state
    }

}
