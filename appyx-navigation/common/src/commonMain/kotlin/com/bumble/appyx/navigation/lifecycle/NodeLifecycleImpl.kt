package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.platform.PlatformLifecycleRegistry
import kotlinx.coroutines.CoroutineScope

internal class NodeLifecycleImpl(lifecycleOwner: CommonLifecycleOwner) : NodeLifecycle {
    private val lifecycleRegistry: PlatformLifecycleRegistry =
        PlatformLifecycleRegistry.create(lifecycleOwner)

    override val lifecycle: CommonLifecycle = lifecycleRegistry
    override val lifecycleScope: CoroutineScope = lifecycleRegistry.coroutineScope

    override fun updateLifecycleState(state: CommonLifecycle.State) {
        lifecycleRegistry.setCurrentState(state)
    }
}
