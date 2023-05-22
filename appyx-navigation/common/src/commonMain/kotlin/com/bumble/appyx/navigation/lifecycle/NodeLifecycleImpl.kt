package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.platform.PlatformLifecycle
import com.bumble.appyx.navigation.platform.PlatformLifecycleOwner
import com.bumble.appyx.navigation.platform.PlatformLifecycleRegistry
import kotlinx.coroutines.CoroutineScope

internal class NodeLifecycleImpl(lifecycleOwner: PlatformLifecycleOwner) : NodeLifecycle {
    private val lifecycleRegistry: PlatformLifecycleRegistry =
        PlatformLifecycleRegistry.create(lifecycleOwner)

    override val lifecycle: PlatformLifecycle = lifecycleRegistry
    override val lifecycleScope: CoroutineScope = lifecycleRegistry.coroutineScope

    override fun updateLifecycleState(state: PlatformLifecycle.State) {
        lifecycleRegistry.setCurrentState(state)
    }
}
