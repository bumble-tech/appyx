package com.bumble.appyx.navigation.lifecycle

import kotlinx.coroutines.CoroutineScope

interface CommonLifecycleOwner {
    val lifecycle: Lifecycle
    val lifecycleScope: CoroutineScope
}
