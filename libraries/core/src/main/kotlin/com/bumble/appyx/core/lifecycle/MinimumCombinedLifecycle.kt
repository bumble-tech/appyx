package com.bumble.appyx.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

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
) : LifecycleOwner {
    private val registry = LifecycleRegistry(this)
    private val lifecycles = ArrayList<Lifecycle>()

    init {
        /*
        Sort list to avoid unnecessary state jumps.
        If Lifecycle(RESUMED) + Lifecycle(DESTROYED) is passed,
        then we should have the final state in DESTROYED state without additional jumping to RESUMED.
         */
        lifecycles.sortedBy { it.currentState }.forEach { manage(it) }
    }

    override val lifecycle: Lifecycle
        get() = registry

    fun manage(lifecycle: Lifecycle) {
        lifecycles += lifecycle
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
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
            ?.takeIf { it.currentState != Lifecycle.State.INITIALIZED }
            ?.also { registry.currentState = it.currentState }
    }

}
