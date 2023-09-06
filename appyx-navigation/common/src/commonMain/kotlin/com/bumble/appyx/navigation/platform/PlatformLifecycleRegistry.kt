package com.bumble.appyx.navigation.platform

import com.bumble.appyx.navigation.lifecycle.CommonLifecycleOwner
import com.bumble.appyx.navigation.lifecycle.Lifecycle

expect open class PlatformLifecycleRegistry() : Lifecycle {
    fun setCurrentState(state: Lifecycle.State)

    companion object {
        fun create(owner: CommonLifecycleOwner): PlatformLifecycleRegistry
    }
}
