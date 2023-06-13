package com.bumble.appyx.navigation.platform

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.navigation.lifecycle.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleEventObserver
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleObserver
import kotlinx.coroutines.CoroutineScope

class AndroidLifecycle(
    val androidLifecycle: androidx.lifecycle.Lifecycle
) : Lifecycle,
    DefaultLifecycleObserver,
    LifecycleEventObserver {

    private val managedDefaultLifecycleObservers: MutableList<DefaultPlatformLifecycleObserver> =
        ArrayList()
    private val managedLifecycleEventObservers: MutableList<PlatformLifecycleEventObserver> =
        ArrayList()

    override val currentState: Lifecycle.State
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

    override fun onStateChanged(source: LifecycleOwner, event: androidx.lifecycle.Lifecycle.Event) {
        val commonEvent = event.toCommonEvent()
        managedLifecycleEventObservers.forEach {
            it.onStateChanged(
                source.lifecycle.currentState.toCommonState(),
                commonEvent
            )
        }
    }
}
