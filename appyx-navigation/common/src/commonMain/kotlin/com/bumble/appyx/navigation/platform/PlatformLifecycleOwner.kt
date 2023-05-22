package com.bumble.appyx.navigation.platform

import kotlinx.coroutines.CoroutineScope

interface PlatformLifecycleOwner {
    val lifecycle: PlatformLifecycle
    val lifecycleScope: CoroutineScope
}
