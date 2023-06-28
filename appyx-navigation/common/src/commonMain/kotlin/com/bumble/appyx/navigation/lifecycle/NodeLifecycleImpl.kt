package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.platform.PlatformLifecycleRegistry
import kotlinx.coroutines.CoroutineScope

internal class NodeLifecycleImpl(lifecycleOwner: CommonLifecycleOwner) : NodeLifecycle {
    private val lifecycleRegistry: PlatformLifecycleRegistry =
        PlatformLifecycleRegistry.create(lifecycleOwner)

    override val lifecycle: Lifecycle = lifecycleRegistry
    override val lifecycleScope: CoroutineScope by lazy { lifecycleRegistry.coroutineScope }

    override fun updateLifecycleState(state: Lifecycle.State) {
        lifecycleRegistry.setCurrentState(state)
    }
}
