package com.bumble.appyx.navigation.platform

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.navigation.lifecycle.CommonLifecycleOwner
import com.bumble.appyx.navigation.lifecycle.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleEventObserver
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleObserver
import kotlinx.coroutines.CoroutineScope

actual class PlatformLifecycleRegistry(
    androidOwner: LifecycleOwner
) : Lifecycle, androidx.lifecycle.DefaultLifecycleObserver,
    androidx.lifecycle.LifecycleEventObserver {

    private var lifecycleOwner: LifecycleOwner? = androidOwner
    private val androidLifecycleRegistry = LifecycleRegistry(androidOwner)

    private val managedDefaultLifecycleObservers: MutableList<DefaultPlatformLifecycleObserver> =
        ArrayList()
    private val managedLifecycleEventObservers: MutableList<PlatformLifecycleEventObserver> =
        ArrayList()

    override val currentState: Lifecycle.State
        get() = androidLifecycleRegistry.currentState.toCommonState()

    actual fun setCurrentState(state: Lifecycle.State) {
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

    override fun onStateChanged(source: LifecycleOwner, event: androidx.lifecycle.Lifecycle.Event) {
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
        if ((owner as? Activity)?.isFinishing == true) {
            lifecycleOwner = null
        }
    }

    actual companion object {
        actual fun create(owner: CommonLifecycleOwner): PlatformLifecycleRegistry =
            PlatformLifecycleRegistry(object : LifecycleOwner {
                override val lifecycle: androidx.lifecycle.Lifecycle
                    get() = when (val platformLifecycle = owner.lifecycle) {
                        is AndroidLifecycle -> platformLifecycle.androidLifecycle
                        is PlatformLifecycleRegistry -> platformLifecycle.androidLifecycleRegistry
                        else -> throw (IllegalStateException(
                            "Unable to get android lifecycle from $platformLifecycle provided by $owner"
                        ))
                    }
            })
    }
}
