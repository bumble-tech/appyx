package com.bumble.appyx.navigation.platform

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.navigation.lifecycle.CommonLifecycle


fun Lifecycle.Event.toCommonEvent(): CommonLifecycle.Event =
    when (this) {
        Lifecycle.Event.ON_CREATE -> CommonLifecycle.Event.ON_CREATE
        Lifecycle.Event.ON_START -> CommonLifecycle.Event.ON_START
        Lifecycle.Event.ON_RESUME -> CommonLifecycle.Event.ON_RESUME
        Lifecycle.Event.ON_PAUSE -> CommonLifecycle.Event.ON_PAUSE
        Lifecycle.Event.ON_STOP -> CommonLifecycle.Event.ON_STOP
        Lifecycle.Event.ON_DESTROY -> CommonLifecycle.Event.ON_DESTROY
        Lifecycle.Event.ON_ANY -> CommonLifecycle.Event.ON_ANY
    }

fun CommonLifecycle.State.toAndroidState(): Lifecycle.State =
    when (this) {
        CommonLifecycle.State.INITIALIZED -> Lifecycle.State.INITIALIZED
        CommonLifecycle.State.CREATED -> Lifecycle.State.CREATED
        CommonLifecycle.State.STARTED -> Lifecycle.State.STARTED
        CommonLifecycle.State.RESUMED -> Lifecycle.State.RESUMED
        CommonLifecycle.State.DESTROYED -> Lifecycle.State.DESTROYED
    }

fun Lifecycle.State.toCommonState(): CommonLifecycle.State =
    when (this) {
        Lifecycle.State.DESTROYED -> CommonLifecycle.State.DESTROYED
        Lifecycle.State.INITIALIZED -> CommonLifecycle.State.INITIALIZED
        Lifecycle.State.CREATED -> CommonLifecycle.State.CREATED
        Lifecycle.State.STARTED -> CommonLifecycle.State.STARTED
        Lifecycle.State.RESUMED -> CommonLifecycle.State.RESUMED
    }