package com.bumble.appyx.navigation.platform

import com.bumble.appyx.navigation.lifecycle.CommonLifecycle
import com.bumble.appyx.navigation.lifecycle.CommonLifecycleOwner

expect class PlatformLifecycleRegistry : CommonLifecycle {
    fun setCurrentState(state: CommonLifecycle.State)

    companion object {
        fun create(owner: CommonLifecycleOwner): PlatformLifecycleRegistry
    }
}
