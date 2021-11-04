package com.github.zsoltk.composeribs.core.routing


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner

internal class UpNavigationDispatcher(
    private val nodeNavigationCallback: UpNavigationCallback,
) {

    private var fallbackUpNavigationCallback: FallbackUpNavigationHandler? = null

    fun interface UpNavigationCallback {
        fun onUpNavigationRequested(): Boolean
    }

    fun upNavigation() {
        if (nodeNavigationCallback.onUpNavigationRequested()) return
        fallbackUpNavigationCallback?.handle()
            ?: throw IllegalStateException("Up navigation callback not set")
    }

    fun setFallbackUpNavigationCallback(callback: FallbackUpNavigationHandler) {
        if (fallbackUpNavigationCallback != null) {
            throw IllegalStateException("Trying to overwrite up navigation callback")
        }
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
    DisposableEffect(lifecycleOwner, upDispatcher) {
        // Add callback to the upDispatcher
        upDispatcher.setFallbackUpNavigationCallback(fallbackUpNavigation)
        // When the effect leaves the Composition, remove the callback
        onDispose {
            upDispatcher.clearFallbackUpNavigationCallback()
        }
    }
}
