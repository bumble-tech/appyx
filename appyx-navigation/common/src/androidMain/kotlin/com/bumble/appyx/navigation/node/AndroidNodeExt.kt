package com.bumble.appyx.navigation.node

import com.bumble.appyx.navigation.platform.PlatformLifecycleRegistry

val AbstractNode.androidLifecycle: androidx.lifecycle.Lifecycle
    get() = (lifecycle as PlatformLifecycleRegistry).androidLifecycleRegistry
