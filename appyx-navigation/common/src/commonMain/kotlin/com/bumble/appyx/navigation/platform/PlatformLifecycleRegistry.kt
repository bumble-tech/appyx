package com.bumble.appyx.navigation.platform

expect class PlatformLifecycleRegistry : PlatformLifecycle {
    fun setCurrentState(state: PlatformLifecycle.State)

    companion object {
        fun create(owner: PlatformLifecycleOwner): PlatformLifecycleRegistry
    }
}
