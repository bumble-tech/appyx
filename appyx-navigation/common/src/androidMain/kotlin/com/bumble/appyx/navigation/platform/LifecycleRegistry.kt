package com.bumble.appyx.navigation.platform

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.Lifecycle.Event as AndroidLifecycleEvent
import androidx.lifecycle.Lifecycle.State as AndroidLifecycleState

actual class LifecycleRegistry(
    private val lifecycleRegistry: LifecycleRegistry
) : Lifecycle,
    androidx.lifecycle.DefaultLifecycleObserver,
    androidx.lifecycle.LifecycleEventObserver {

    override var currentState: Lifecycle.State
        get() = lifecycleRegistry.currentState.toCommonState()
        set(value) {
            lifecycleRegistry.currentState = value.toAndroidState()
        }

    override val coroutineScope: CoroutineScope = lifecycleRegistry.coroutineScope

    private val managedDefaultLifecycleObservers: MutableList<DefaultLifecycleObserver> =
        ArrayList()
    private val managedLifecycleEventObservers: MutableList<LifecycleEventObserver> =
        ArrayList()

    init {
        lifecycleRegistry.addObserver(this)
    }

    override fun addObserver(observer: LifecycleObserver) {
        when (observer) {
            is DefaultLifecycleObserver -> managedDefaultLifecycleObservers.add(observer)
            is LifecycleEventObserver -> managedLifecycleEventObservers.add(observer)
        }
    }

    override fun removeObserver(observer: LifecycleObserver) {
        when (observer) {
            is DefaultLifecycleObserver -> managedDefaultLifecycleObservers.remove(observer)
            is LifecycleEventObserver -> managedLifecycleEventObservers.remove(observer)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: androidx.lifecycle.Lifecycle.Event) {
        val commonEvent = event.toCommonEvent()
        managedLifecycleEventObservers.forEach { it.onStateChanged(commonEvent) }
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
}

private fun AndroidLifecycleEvent.toCommonEvent(): Lifecycle.Event =
    when (this) {
        AndroidLifecycleEvent.ON_CREATE -> Lifecycle.Event.ON_CREATE
        AndroidLifecycleEvent.ON_START -> Lifecycle.Event.ON_START
        AndroidLifecycleEvent.ON_RESUME -> Lifecycle.Event.ON_RESUME
        AndroidLifecycleEvent.ON_PAUSE -> Lifecycle.Event.ON_PAUSE
        AndroidLifecycleEvent.ON_STOP -> Lifecycle.Event.ON_STOP
        AndroidLifecycleEvent.ON_DESTROY -> Lifecycle.Event.ON_DESTROY
        AndroidLifecycleEvent.ON_ANY -> Lifecycle.Event.ON_ANY
    }

private fun Lifecycle.State.toAndroidState(): AndroidLifecycleState =
    when (this) {
        Lifecycle.State.INITIALIZED -> AndroidLifecycleState.INITIALIZED
        Lifecycle.State.CREATED -> AndroidLifecycleState.CREATED
        Lifecycle.State.STARTED -> AndroidLifecycleState.STARTED
        Lifecycle.State.RESUMED -> AndroidLifecycleState.RESUMED
        Lifecycle.State.DESTROYED -> AndroidLifecycleState.DESTROYED
    }

private fun AndroidLifecycleState.toCommonState(): Lifecycle.State =
    when (this) {
        AndroidLifecycleState.DESTROYED -> Lifecycle.State.DESTROYED
        AndroidLifecycleState.INITIALIZED -> Lifecycle.State.INITIALIZED
        AndroidLifecycleState.CREATED -> Lifecycle.State.CREATED
        AndroidLifecycleState.STARTED -> Lifecycle.State.STARTED
        AndroidLifecycleState.RESUMED -> Lifecycle.State.RESUMED
    }
