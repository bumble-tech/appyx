package com.bumble.appyx.navigation.lifecycle

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
    vararg lifecycles: Lifecycle,
) : CommonLifecycleOwner {
    private val registry = PlatformLifecycleRegistry.create(this)
    private val lifecycles = ArrayList<Lifecycle>()

    init {
        /*
        Sort list to avoid unnecessary state jumps.
        If Lifecycle(RESUMED) + Lifecycle(DESTROYED) is passed,
        then we should have the final state in DESTROYED state without additional jumping to RESUMED.
         */
        lifecycles.sortedBy { it.currentState }.forEach { manage(it) }
    }

    override val lifecycle: Lifecycle = registry
    override val lifecycleScope: CoroutineScope = registry.coroutineScope

    fun manage(lifecycle: Lifecycle) {
        lifecycles += lifecycle
        lifecycle.addObserver(object : DefaultPlatformLifecycleObserver {
            override fun onCreate() {
                update()
            }

            override fun onStart() {
                update()
            }

            override fun onResume() {
                update()
            }

            override fun onPause() {
                update()
            }

            override fun onStop() {
                update()
            }

            override fun onDestroy() {
                update()
            }
        })
        update()
    }

    private fun update() {
        lifecycles
            .minByOrNull { it.currentState }
            ?.takeIf { it.currentState != Lifecycle.State.INITIALIZED }
            ?.also { registry.setCurrentState(it.currentState) }
    }

}
