package com.bumble.appyx.navigation.platform

import com.bumble.appyx.navigation.lifecycle.Lifecycle


fun androidx.lifecycle.Lifecycle.Event.toCommonEvent(): Lifecycle.Event =
    when (this) {
        androidx.lifecycle.Lifecycle.Event.ON_CREATE -> Lifecycle.Event.ON_CREATE
        androidx.lifecycle.Lifecycle.Event.ON_START -> Lifecycle.Event.ON_START
        androidx.lifecycle.Lifecycle.Event.ON_RESUME -> Lifecycle.Event.ON_RESUME
        androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> Lifecycle.Event.ON_PAUSE
        androidx.lifecycle.Lifecycle.Event.ON_STOP -> Lifecycle.Event.ON_STOP
        androidx.lifecycle.Lifecycle.Event.ON_DESTROY -> Lifecycle.Event.ON_DESTROY
        androidx.lifecycle.Lifecycle.Event.ON_ANY -> Lifecycle.Event.ON_ANY
    }

fun Lifecycle.State.toAndroidState(): androidx.lifecycle.Lifecycle.State =
    when (this) {
        Lifecycle.State.INITIALIZED -> androidx.lifecycle.Lifecycle.State.INITIALIZED
        Lifecycle.State.CREATED -> androidx.lifecycle.Lifecycle.State.CREATED
        Lifecycle.State.STARTED -> androidx.lifecycle.Lifecycle.State.STARTED
        Lifecycle.State.RESUMED -> androidx.lifecycle.Lifecycle.State.RESUMED
        Lifecycle.State.DESTROYED -> androidx.lifecycle.Lifecycle.State.DESTROYED
    }

fun androidx.lifecycle.Lifecycle.State.toCommonState(): Lifecycle.State =
    when (this) {
        androidx.lifecycle.Lifecycle.State.DESTROYED -> Lifecycle.State.DESTROYED
        androidx.lifecycle.Lifecycle.State.INITIALIZED -> Lifecycle.State.INITIALIZED
        androidx.lifecycle.Lifecycle.State.CREATED -> Lifecycle.State.CREATED
        androidx.lifecycle.Lifecycle.State.STARTED -> Lifecycle.State.STARTED
        androidx.lifecycle.Lifecycle.State.RESUMED -> Lifecycle.State.RESUMED
    }
