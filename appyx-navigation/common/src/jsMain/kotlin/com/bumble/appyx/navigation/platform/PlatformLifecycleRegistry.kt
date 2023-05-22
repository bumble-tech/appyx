package com.bumble.appyx.navigation.platform

import kotlinx.coroutines.CoroutineScope

actual class PlatformLifecycleRegistry(
    lifecycleCoroutineScope: CoroutineScope,
) : PlatformLifecycle {

    private val managedDefaultLifecycleObservers: MutableList<DefaultPlatformLifecycleObserver> =
        ArrayList()
    private val managedLifecycleEventObservers: MutableList<PlatformLifecycleEventObserver> =
        ArrayList()

    private var _currentState: PlatformLifecycle.State = PlatformLifecycle.State.INITIALIZED
    override var currentState: PlatformLifecycle.State
        get() = _currentState
        set(value) {
            when (value) {
                PlatformLifecycle.State.INITIALIZED -> Unit
                PlatformLifecycle.State.CREATED -> {
                    managedDefaultLifecycleObservers.forEach { it.onCreate() }
                    managedLifecycleEventObservers.forEach {
                        it.onStateChanged(
                            value,
                            PlatformLifecycle.Event.ON_CREATE
                        )
                    }
                }
                PlatformLifecycle.State.STARTED -> {
                    managedDefaultLifecycleObservers.forEach { it.onStart() }
                    managedLifecycleEventObservers.forEach {
                        it.onStateChanged(
                            value,
                            PlatformLifecycle.Event.ON_START
                        )
                    }
                }
                PlatformLifecycle.State.RESUMED -> {
                    managedDefaultLifecycleObservers.forEach { it.onResume() }
                    managedLifecycleEventObservers.forEach {
                        it.onStateChanged(
                            value,
                            PlatformLifecycle.Event.ON_RESUME
                        )
                    }
                }
                PlatformLifecycle.State.DESTROYED -> {
                    managedDefaultLifecycleObservers.forEach { it.onDestroy() }
                    managedLifecycleEventObservers.forEach {
                        it.onStateChanged(
                            value,
                            PlatformLifecycle.Event.ON_DESTROY
                        )
                    }
                }
            }
            _currentState = value
        }

    override val coroutineScope: CoroutineScope = lifecycleCoroutineScope

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

    actual fun setCurrentState(state: PlatformLifecycle.State) {
    }

    actual companion object {
        actual fun create(owner: PlatformLifecycleOwner): PlatformLifecycleRegistry {
            TODO("Not yet implemented")
        }
    }
}