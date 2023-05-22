package com.bumble.appyx.navigation.platform

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope

actual class PlatformLifecycleRegistry(
    val androidLifecycleRegistry: LifecycleRegistry
) : PlatformLifecycle, androidx.lifecycle.DefaultLifecycleObserver,
    androidx.lifecycle.LifecycleEventObserver {

    private val managedDefaultLifecycleObservers: MutableList<DefaultPlatformLifecycleObserver> =
        ArrayList()
    private val managedLifecycleEventObservers: MutableList<PlatformLifecycleEventObserver> =
        ArrayList()

    override val currentState: PlatformLifecycle.State
        get() = androidLifecycleRegistry.currentState.toCommonState()

    actual fun setCurrentState(state: PlatformLifecycle.State) {
        androidLifecycleRegistry.currentState = state.toAndroidState()
    }

    override val coroutineScope: CoroutineScope = androidLifecycleRegistry.coroutineScope

    init {
        androidLifecycleRegistry.addObserver(this)
    }

    override fun addObserver(observer: PlatformLifecycleObserver) {
        when (observer) {
            is DefaultPlatformLifecycleObserver -> managedDefaultLifecycleObservers.add(observer)
            is PlatformLifecycleEventObserver -> managedLifecycleEventObservers.add(observer)
        }
    }

    override fun removeObserver(observer: PlatformLifecycleObserver) {
        when (observer) {
            is DefaultPlatformLifecycleObserver -> managedDefaultLifecycleObservers.remove(observer)
            is PlatformLifecycleEventObserver -> managedLifecycleEventObservers.remove(observer)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val commonEvent = event.toCommonEvent()
        managedLifecycleEventObservers.forEach {
            it.onStateChanged(
                source.lifecycle.currentState.toCommonState(), commonEvent
            )
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        managedDefaultLifecycleObservers.forEach { it.onCreate() }
    }

    override fun onStart(owner: LifecycleOwner) {
        managedDefaultLifecycleObservers.forEach { it.onStart() }
    }

    override fun onResume(owner: LifecycleOwner) {
        managedDefaultLifecycleObservers.forEach { it.onResume() }
    }

    override fun onPause(owner: LifecycleOwner) {
        managedDefaultLifecycleObservers.forEach { it.onPause() }
    }

    override fun onStop(owner: LifecycleOwner) {
        managedDefaultLifecycleObservers.forEach { it.onStop() }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        managedDefaultLifecycleObservers.forEach { it.onDestroy() }
    }

    actual companion object {
        actual fun create(owner: PlatformLifecycleOwner): PlatformLifecycleRegistry =
            PlatformLifecycleRegistry(LifecycleRegistry(object : LifecycleOwner {
                override val lifecycle: Lifecycle
                    get() = when (val platformLifecycle = owner.lifecycle) {
                        is AndroidPlatformLifecycle -> platformLifecycle.androidLifecycle
                        is PlatformLifecycleRegistry -> platformLifecycle.androidLifecycleRegistry
                        else -> throw (IllegalStateException(
                            "Unable to get android lifecycle from $platformLifecycle provided by $owner"
                        ))
                    }
            }))
    }
}
