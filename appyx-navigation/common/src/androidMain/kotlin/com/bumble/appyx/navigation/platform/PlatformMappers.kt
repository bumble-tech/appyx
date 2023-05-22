package com.bumble.appyx.navigation.platform

import androidx.lifecycle.Lifecycle


fun Lifecycle.Event.toCommonEvent(): PlatformLifecycle.Event =
    when (this) {
        Lifecycle.Event.ON_CREATE -> PlatformLifecycle.Event.ON_CREATE
        Lifecycle.Event.ON_START -> PlatformLifecycle.Event.ON_START
        Lifecycle.Event.ON_RESUME -> PlatformLifecycle.Event.ON_RESUME
        Lifecycle.Event.ON_PAUSE -> PlatformLifecycle.Event.ON_PAUSE
        Lifecycle.Event.ON_STOP -> PlatformLifecycle.Event.ON_STOP
        Lifecycle.Event.ON_DESTROY -> PlatformLifecycle.Event.ON_DESTROY
        Lifecycle.Event.ON_ANY -> PlatformLifecycle.Event.ON_ANY
    }

fun PlatformLifecycle.State.toAndroidState(): Lifecycle.State =
    when (this) {
        PlatformLifecycle.State.INITIALIZED -> Lifecycle.State.INITIALIZED
        PlatformLifecycle.State.CREATED -> Lifecycle.State.CREATED
        PlatformLifecycle.State.STARTED -> Lifecycle.State.STARTED
        PlatformLifecycle.State.RESUMED -> Lifecycle.State.RESUMED
        PlatformLifecycle.State.DESTROYED -> Lifecycle.State.DESTROYED
    }

fun Lifecycle.State.toCommonState(): PlatformLifecycle.State =
    when (this) {
        Lifecycle.State.DESTROYED -> PlatformLifecycle.State.DESTROYED
        Lifecycle.State.INITIALIZED -> PlatformLifecycle.State.INITIALIZED
        Lifecycle.State.CREATED -> PlatformLifecycle.State.CREATED
        Lifecycle.State.STARTED -> PlatformLifecycle.State.STARTED
        Lifecycle.State.RESUMED -> PlatformLifecycle.State.RESUMED
    }