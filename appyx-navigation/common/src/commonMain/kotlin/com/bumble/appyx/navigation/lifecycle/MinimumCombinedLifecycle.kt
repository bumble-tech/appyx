package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.platform.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.platform.PlatformLifecycle
import com.bumble.appyx.navigation.platform.PlatformLifecycleOwner
import com.bumble.appyx.navigation.platform.PlatformLifecycleRegistry
import kotlinx.coroutines.CoroutineScope


/**
 * Combines multiple lifecycles and provides a minimum of their states.
 *
 * For example:
 * - RESUMED + STARTED + RESUMED -> STARTED
 * - CREATED + RESUMED + DESTROYED -> DESTROYED
 * - INITIALIZED + DESTROYED -> DESTROYED
 */
internal class MinimumCombinedLifecycle(
    vararg lifecycles: PlatformLifecycle,
) : PlatformLifecycleOwner {
    private val registry = PlatformLifecycleRegistry.create(this)
    private val lifecycles = ArrayList<PlatformLifecycle>()

    init {
        /*
        Sort list to avoid unnecessary state jumps.
        If Lifecycle(RESUMED) + Lifecycle(DESTROYED) is passed,
        then we should have the final state in DESTROYED state without additional jumping to RESUMED.
         */
        lifecycles.sortedBy { it.currentState }.forEach { manage(it) }
    }

    override val lifecycle: PlatformLifecycle = registry
    override val lifecycleScope: CoroutineScope = registry.coroutineScope

    fun manage(lifecycle: PlatformLifecycle) {
        lifecycles += lifecycle
        lifecycle.addObserver(object : DefaultPlatformLifecycleObserver {
            override fun onCreate() {
                update()
            }

            override fun onStart(owner: LifecycleOwner) {
                update()
            }

            override fun onResume(owner: LifecycleOwner) {
                update()
            }

            override fun onPause(owner: LifecycleOwner) {
                update()
            }

            override fun onStop(owner: LifecycleOwner) {
                update()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                update()
            }
        })
        update()
    }

    private fun update() {
        lifecycles
            .minByOrNull { it.currentState }
            ?.takeIf { it.currentState != PlatformLifecycle.State.INITIALIZED }
            ?.also { registry.setCurrentState(it.currentState) }
    }

}
