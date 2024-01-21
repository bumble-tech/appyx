package com.bumble.appyx.navigation.platform

import com.bumble.appyx.navigation.lifecycle.Lifecycle

class LifecycleHelper {
    val lifecycle: PlatformLifecycleRegistry = PlatformLifecycleRegistry()

    fun created() {
        lifecycle.setCurrentState(Lifecycle.State.CREATED)
    }

    fun resumed() {
        lifecycle.setCurrentState(Lifecycle.State.RESUMED)
    }

    fun started() {
        lifecycle.setCurrentState(Lifecycle.State.STARTED)
    }

    fun destroyed() {
        lifecycle.setCurrentState(Lifecycle.State.DESTROYED)
    }
}
