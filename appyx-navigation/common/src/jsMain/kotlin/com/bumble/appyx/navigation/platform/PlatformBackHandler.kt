package com.bumble.appyx.navigation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Suppress("CompositionLocalAllowlist")
val LocalOnBackPressedDispatcherOwner: ProvidableCompositionLocal<OnBackPressedDispatcherOwner> =
    compositionLocalOf {
        object : OnBackPressedDispatcherOwner {
            override val onBackPressedDispatcher: OnBackPressedDispatcher
                get() = OnBackPressedDispatcher(null)
        }
    }

interface OnBackPressedDispatcherOwner {
    val onBackPressedDispatcher: OnBackPressedDispatcher
}

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    // Safely update the current `onBack` lambda when a new one is provided
    val currentOnBack by rememberUpdatedState(onBack)
    // Remember in Composition a back callback that calls the `onBack` lambda
    val backCallback = remember<OnBackPressedCallback> {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentOnBack()
            }
        }
    }
    // On every successful composition, update the callback with the `enabled` value
    SideEffect {
        backCallback.isEnabled = enabled
    }

    // register for back events only whilst present in the composition
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher
    DisposableEffect(backDispatcher) {
        val cancellable = backDispatcher.addCancellableCallback(backCallback)

        onDispose {
            cancellable.cancel()
        }
    }
}
