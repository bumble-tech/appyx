package com.bumble.appyx.navigation.lifecycle

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun CommonLifecycleOwner.asFlow(): Flow<CommonLifecycle.State> =
    lifecycle.asFlow()

fun CommonLifecycle.asFlow(): Flow<CommonLifecycle.State> =
    callbackFlow {
        val observer = PlatformLifecycleEventObserver { currentState, _ ->
            trySend(currentState)
        }
        trySend(currentState)
        addObserver(observer)
        awaitClose { removeObserver(observer) }
    }

internal val CommonLifecycle.isDestroyed: Boolean
    get() = currentState == CommonLifecycle.State.DESTROYED

fun CommonLifecycle.subscribe(
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
