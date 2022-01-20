package com.bumble.appyx.v2.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class TestLifecycle() : LifecycleOwner {
    private val registry = LifecycleRegistry(this)

    constructor(state: Lifecycle.State) : this() {
        this.state = state
    }

    var state: Lifecycle.State
        get() = registry.currentState
        set(value) {
            registry.currentState = value
        }

    override fun getLifecycle(): Lifecycle = registry

}
