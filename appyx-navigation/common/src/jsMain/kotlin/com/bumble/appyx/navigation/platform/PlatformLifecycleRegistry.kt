package com.bumble.appyx.navigation.platform

import com.bumble.appyx.navigation.lifecycle.CommonLifecycleOwner
import com.bumble.appyx.navigation.lifecycle.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleEventObserver
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleObserver
import kotlinx.coroutines.CoroutineScope

actual class PlatformLifecycleRegistry(
    lifecycleCoroutineScope: CoroutineScope,
) : Lifecycle {

    private val managedDefaultLifecycleObservers: MutableList<DefaultPlatformLifecycleObserver> =
        ArrayList()
    private val managedLifecycleEventObservers: MutableList<PlatformLifecycleEventObserver> =
        ArrayList()

    private var _currentState: Lifecycle.State = Lifecycle.State.INITIALIZED
    override var currentState: Lifecycle.State
        get() = _currentState
        set(value) {
            when (value) {
                Lifecycle.State.INITIALIZED -> Unit
                Lifecycle.State.CREATED -> {
                    managedDefaultLifecycleObservers.forEach { it.onCreate() }
                    managedLifecycleEventObservers.forEach {
                        it.onStateChanged(
                            value,
                            Lifecycle.Event.ON_CREATE
                        )
                    }
                }
                Lifecycle.State.STARTED -> {
                    managedDefaultLifecycleObservers.forEach { it.onStart() }
                    managedLifecycleEventObservers.forEach {
                        it.onStateChanged(
                            value,
                            Lifecycle.Event.ON_START
                        )
                    }
                }
                Lifecycle.State.RESUMED -> {
                    managedDefaultLifecycleObservers.forEach { it.onResume() }
                    managedLifecycleEventObservers.forEach {
                        it.onStateChanged(
                            value,
                            Lifecycle.Event.ON_RESUME
                        )
                    }
                }
                Lifecycle.State.DESTROYED -> {
                    managedDefaultLifecycleObservers.forEach { it.onDestroy() }
                    managedLifecycleEventObservers.forEach {
                        it.onStateChanged(
                            value,
                            Lifecycle.Event.ON_DESTROY
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

    actual fun setCurrentState(state: Lifecycle.State) {
        currentState = state
    }

    actual companion object {
        actual fun create(owner: CommonLifecycleOwner): PlatformLifecycleRegistry =
            PlatformLifecycleRegistry(owner.lifecycleScope)
    }
}
