package com.bumble.appyx.navigation.platform

import com.bumble.appyx.navigation.lifecycle.CommonLifecycle
import com.bumble.appyx.navigation.lifecycle.CommonLifecycleOwner
import com.bumble.appyx.navigation.lifecycle.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleEventObserver
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleObserver
import kotlinx.coroutines.CoroutineScope


actual class PlatformLifecycleRegistry(
    lifecycleCoroutineScope: CoroutineScope,
) : CommonLifecycle {

    private val managedDefaultLifecycleObservers: MutableList<DefaultPlatformLifecycleObserver> =
        ArrayList()
    private val managedLifecycleEventObservers: MutableList<PlatformLifecycleEventObserver> =
        ArrayList()

    private var _currentState: CommonLifecycle.State = CommonLifecycle.State.INITIALIZED
    override val currentState: CommonLifecycle.State
        get() = _currentState

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

    actual fun setCurrentState(state: CommonLifecycle.State) {
        when (state) {
            CommonLifecycle.State.INITIALIZED -> Unit
            CommonLifecycle.State.CREATED -> {
                managedDefaultLifecycleObservers.forEach { it.onCreate() }
                managedLifecycleEventObservers.forEach {
                    it.onStateChanged(
                        state,
                        CommonLifecycle.Event.ON_CREATE
                    )
                }
            }
            CommonLifecycle.State.STARTED -> {
                managedDefaultLifecycleObservers.forEach { it.onStart() }
                managedLifecycleEventObservers.forEach {
                    it.onStateChanged(
                        state,
                        CommonLifecycle.Event.ON_START
                    )
                }
            }
            CommonLifecycle.State.RESUMED -> {
                managedDefaultLifecycleObservers.forEach { it.onResume() }
                managedLifecycleEventObservers.forEach {
                    it.onStateChanged(
                        state,
                        CommonLifecycle.Event.ON_RESUME
                    )
                }
            }
            CommonLifecycle.State.DESTROYED -> {
                managedDefaultLifecycleObservers.forEach { it.onDestroy() }
                managedLifecycleEventObservers.forEach {
                    it.onStateChanged(
                        state,
                        CommonLifecycle.Event.ON_DESTROY
                    )
                }
            }
        }
        _currentState = state
    }

    actual companion object {
        actual fun create(owner: CommonLifecycleOwner): PlatformLifecycleRegistry =
            PlatformLifecycleRegistry(owner.lifecycleScope)
    }
}
