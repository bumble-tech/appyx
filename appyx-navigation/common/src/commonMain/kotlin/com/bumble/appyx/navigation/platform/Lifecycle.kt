package com.bumble.appyx.navigation.platform

import kotlinx.coroutines.CoroutineScope

interface Lifecycle {
    var currentState: State

    val coroutineScope: CoroutineScope

    fun addObserver(observer: LifecycleObserver)

    fun removeObserver(observer: LifecycleObserver)

    enum class State {
        INITIALIZED,
        CREATED,
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
