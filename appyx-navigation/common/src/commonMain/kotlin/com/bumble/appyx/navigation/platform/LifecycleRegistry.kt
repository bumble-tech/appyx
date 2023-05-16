package com.bumble.appyx.navigation.platform

interface LifecycleRegistry : Lifecycle {
    fun setCurrentState(state: Lifecycle.State)
}

interface LifecycleRegistryProvider : (LifecycleOwner) -> LifecycleRegistry
