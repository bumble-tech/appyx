package com.bumble.appyx.navigation.platform

import com.bumble.appyx.navigation.node.Node

val Node.androidLifecycle: androidx.lifecycle.Lifecycle
    get() = (lifecycle as PlatformLifecycleRegistry).androidLifecycleRegistry
