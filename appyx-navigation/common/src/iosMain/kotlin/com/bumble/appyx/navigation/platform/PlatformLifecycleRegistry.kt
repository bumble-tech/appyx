package com.bumble.appyx.navigation.platform

import com.bumble.appyx.navigation.lifecycle.CommonLifecycleOwner
import com.bumble.appyx.navigation.lifecycle.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleEventObserver
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

actual class PlatformLifecycleRegistry : Lifecycle {

    private val managedDefaultLifecycleObservers: MutableList<DefaultPlatformLifecycleObserver> =
        ArrayList()
    private val managedLifecycleEventObservers: MutableList<PlatformLifecycleEventObserver> =
        ArrayList()

    private var _currentState: Lifecycle.State = Lifecycle.State.INITIALIZED
    override val currentState: Lifecycle.State
        get() = _currentState

    override val coroutineScope: CoroutineScope by lazy { MainScope() }

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

    actual fun setCurrentState(state: Lifecycle.State) {
        when (state) {
            Lifecycle.State.INITIALIZED -> Unit
            Lifecycle.State.CREATED -> {
                managedDefaultLifecycleObservers.forEach { it.onCreate() }
                managedLifecycleEventObservers.forEach {
                    it.onStateChanged(
                        state,
                        Lifecycle.Event.ON_CREATE
                    )
                }
            }
            Lifecycle.State.STARTED -> {
                managedDefaultLifecycleObservers.forEach { it.onStart() }
                managedLifecycleEventObservers.forEach {
                    it.onStateChanged(
                        state,
                        Lifecycle.Event.ON_START
                    )
                }
            }
            Lifecycle.State.RESUMED -> {
                managedDefaultLifecycleObservers.forEach { it.onResume() }
                managedLifecycleEventObservers.forEach {
                    it.onStateChanged(
                        state,
                        Lifecycle.Event.ON_RESUME
                    )
                }
            }
            Lifecycle.State.DESTROYED -> {
                managedDefaultLifecycleObservers.forEach { it.onDestroy() }
                managedLifecycleEventObservers.forEach {
                    it.onStateChanged(
                        state,
                        Lifecycle.Event.ON_DESTROY
                    )
                }
                if (coroutineScope.isActive) coroutineScope.cancel("lifecycle was destroyed")
            }
        }
        _currentState = state
    }

    actual companion object {
        actual fun create(owner: CommonLifecycleOwner): PlatformLifecycleRegistry =
            PlatformLifecycleRegistry()
    }
}