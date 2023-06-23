package com.bumble.appyx.interactions.core.ui.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.bumble.appyx.interactions.core.model.AppyxComponent

@Composable
fun AppyxComponentSetup(appyxComponent: AppyxComponent<*, *>) {
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(appyxComponent) {
        appyxComponent.onAddedToComposition(coroutineScope)
        onDispose {
            appyxComponent.onRemovedFromComposition()
        }
    }
}
