package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.platform.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.platform.PlatformLifecycle
import com.bumble.appyx.navigation.platform.PlatformLifecycleEventObserver
import com.bumble.appyx.navigation.platform.PlatformLifecycleOwner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun PlatformLifecycleOwner.asFlow(): Flow<PlatformLifecycle.State> =
    lifecycle.asFlow()

fun PlatformLifecycle.asFlow(): Flow<PlatformLifecycle.State> =
    callbackFlow {
        val observer = PlatformLifecycleEventObserver { currentState, _ ->
            trySend(currentState)
        }
        trySend(currentState)
        addObserver(observer)
        awaitClose { removeObserver(observer) }
    }

internal val PlatformLifecycle.isDestroyed: Boolean
    get() = currentState == PlatformLifecycle.State.DESTROYED

fun PlatformLifecycle.subscribe(
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {}
) {
    addObserver(
        object : DefaultPlatformLifecycleObserver {
            override fun onCreate() {
                onCreate()
            }

            override fun onStart() {
                onStart()
            }

            override fun onResume() {
                onResume()
            }

            override fun onPause() {
                onPause()
            }

            override fun onStop() {
                onStop()
            }

            override fun onDestroy() {
                onDestroy()
            }
        }
    )
}
