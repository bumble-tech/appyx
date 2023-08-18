package com.bumble.appyx.navigation.lifecycle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface Lifecycle {
    val currentState: State

    val coroutineScope: CoroutineScope

    fun addObserver(observer: PlatformLifecycleObserver)

    fun removeObserver(observer: PlatformLifecycleObserver)

    fun asFlow(): Flow<State> =
        callbackFlow {
            val observer = PlatformLifecycleEventObserver { state, _ ->
                trySend(state)
            }
            trySend(currentState)
            addObserver(observer)
            awaitClose { removeObserver(observer) }
        }

    enum class State {
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED,
        DESTROYED,
    }

    enum class Event {
        ON_CREATE,
        ON_START,
        ON_RESUME,
        ON_PAUSE,
        ON_STOP,
        ON_DESTROY,
        ON_ANY,
    }
}
