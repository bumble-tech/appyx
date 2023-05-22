package com.bumble.appyx.navigation.platform

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope

internal class AndroidPlatformLifecycle(
    val androidLifecycle: Lifecycle
) : PlatformLifecycle,
    DefaultLifecycleObserver,
    LifecycleEventObserver {

    private val managedDefaultLifecycleObservers: MutableList<DefaultPlatformLifecycleObserver> =
        ArrayList()
    private val managedLifecycleEventObservers: MutableList<PlatformLifecycleEventObserver> =
        ArrayList()

    override val currentState: PlatformLifecycle.State
        get() = androidLifecycle.currentState.toCommonState()

    override val coroutineScope: CoroutineScope =
        androidLifecycle.coroutineScope

    init {
        androidLifecycle.addObserver(this)
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

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val commonEvent = event.toCommonEvent()
        managedLifecycleEventObservers.forEach {
            it.onStateChanged(
                source.lifecycle.currentState.toCommonState(),
                commonEvent
            )
        }
    }
}
