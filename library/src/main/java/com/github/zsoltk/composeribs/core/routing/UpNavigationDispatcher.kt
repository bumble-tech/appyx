package com.github.zsoltk.composeribs.core.routing


import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler

internal val LocalUpNavigationDispatcher: ProvidableCompositionLocal<UpNavigationDispatcher> =
    compositionLocalOf { throw IllegalStateException("UpNavigationDispatcher is not initialised") }

internal class UpNavigationDispatcher {

    private val upNavigationCallbacks = mutableListOf<UpNavigationCallback>()

    interface UpNavigationCallback {
        fun onUpNavigationRequested()
    }

    fun upNavigation() {
        upNavigationCallbacks.forEach { it.onUpNavigationRequested() }
    }

    fun addUpNavigationCallback(upNavigationCallback: UpNavigationCallback) {
        upNavigationCallbacks.add(upNavigationCallback)
    }

    fun removeUpNavigationCallback(upNavigationCallback: UpNavigationCallback) {
        upNavigationCallbacks.remove(upNavigationCallback)
    }
}
//fun upNavigation(fallbackUpNavigationHandler: FallbackUpNavigationHandler) {
//    parent?.handleUpNavigation(fallbackUpNavigationHandler) ?: fallbackUpNavigationHandler.handle()
//}
//
//private fun handleUpNavigation(fallbackUpNavigationHandler: FallbackUpNavigationHandler) {
//    val subtreeHandlers = plugins.filterIsInstance<UpNavigationHandler>()
//
//    if (subtreeHandlers.none { it.handleUpNavigation() }) {
//        upNavigation(fallbackUpNavigationHandler)
//    }
//}

@Composable
internal fun UpHandler(
    nodeUpNavigation: () -> Boolean,
    fallbackUpNavigation: () -> Unit,
) {
    val currentNodeUpNavigation by rememberUpdatedState(nodeUpNavigation)
    val currentFallbackUpNavigation by rememberUpdatedState(fallbackUpNavigation)

    val upCallback = remember {
        object : UpNavigationDispatcher.UpNavigationCallback {
            override fun onUpNavigationRequested() {
                if (!currentNodeUpNavigation()) {
                    currentFallbackUpNavigation()
                }
            }
        }
    }

    val upDispatcher = checkNotNull(LocalUpNavigationDispatcher.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, upDispatcher) {
        // Add callback to the backDispatcher
        upDispatcher.addUpNavigationCallback(upCallback)
        // When the effect leaves the Composition, remove the callback
        onDispose {
            upDispatcher.removeUpNavigationCallback(upCallback)
        }
    }
}

