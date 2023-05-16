package com.bumble.appyx.navigation.platform

import kotlinx.coroutines.CoroutineScope

interface LifecycleOwner {
    val lifecycle: Lifecycle
    val lifecycleScope: CoroutineScope
}

interface LocalLifecycleOwnerProvider : () -> LifecycleOwner
