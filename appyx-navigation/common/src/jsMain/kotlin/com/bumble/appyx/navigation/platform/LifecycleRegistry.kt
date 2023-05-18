package com.bumble.appyx.navigation.platform

import kotlinx.coroutines.CoroutineScope

actual class LifecycleRegistry(
    lifecycleCoroutineScope: CoroutineScope,
) : Lifecycle {

    private val managedDefaultLifecycleObservers: MutableList<DefaultLifecycleObserver> =
        ArrayList()
    private val managedLifecycleEventObservers: MutableList<LifecycleEventObserver> =
        ArrayList()

    private var _currentState: Lifecycle.State = Lifecycle.State.INITIALIZED
    override var currentState: Lifecycle.State
        get() = _currentState
        set(value) {
            when (value) {
                Lifecycle.State.INITIALIZED -> Unit
                Lifecycle.State.CREATED -> {
                    managedDefaultLifecycleObservers.forEach { it.onCreate() }
                    managedLifecycleEventObservers.forEach { it.onStateChanged(Lifecycle.Event.ON_CREATE) }
                }
                Lifecycle.State.STARTED -> {
                    managedDefaultLifecycleObservers.forEach { it.onStart() }
                    managedLifecycleEventObservers.forEach { it.onStateChanged(Lifecycle.Event.ON_START) }
                }
                Lifecycle.State.RESUMED -> {
                    managedDefaultLifecycleObservers.forEach { it.onResume() }
                    managedLifecycleEventObservers.forEach { it.onStateChanged(Lifecycle.Event.ON_RESUME) }
                }
                Lifecycle.State.DESTROYED -> {
                    managedDefaultLifecycleObservers.forEach { it.onDestroy() }
                    managedLifecycleEventObservers.forEach { it.onStateChanged(Lifecycle.Event.ON_DESTROY) }
                }
            }
            _currentState = value
        }

    override val coroutineScope: CoroutineScope = lifecycleCoroutineScope

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
}