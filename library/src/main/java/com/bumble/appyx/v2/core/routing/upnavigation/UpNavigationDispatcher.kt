package com.bumble.appyx.v2.core.routing.upnavigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner

internal class UpNavigationDispatcher(
    private val nodeNavigationCallback: UpNavigationCallback,
) {

    private var fallbackUpNavigationCallback: FallbackUpNavigationHandler? = null

    fun interface UpNavigationCallback {
        fun handleUpNavigation(): Boolean
    }

    fun upNavigation() {
        if (nodeNavigationCallback.handleUpNavigation()) return
        fallbackUpNavigationCallback?.handleUpNavigation()
            ?: throw IllegalStateException("Up navigation callback not set")
    }

    fun setFallbackUpNavigationCallback(callback: FallbackUpNavigationHandler) {
        this.fallbackUpNavigationCallback = callback
    }

    fun clearFallbackUpNavigationCallback() {
        fallbackUpNavigationCallback = null
    }
}

@Composable
internal fun UpHandler(
    upDispatcher: UpNavigationDispatcher,
    fallbackUpNavigation: FallbackUpNavigationHandler,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, upDispatcher, fallbackUpNavigation) {
        // Add callback to the upDispatcher
        upDispatcher.setFallbackUpNavigationCallback(fallbackUpNavigation)
        // When the effect leaves the Composition, remove the callback
        onDispose {
            upDispatcher.clearFallbackUpNavigationCallback()
        }
    }
}
