package com.bumble.appyx.interactions.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.bumble.appyx.interactions.core.InteractionModel

@Composable
fun InteractionModelSetup(interactionModel: InteractionModel<*, *>) {
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(interactionModel) {
        interactionModel.onAddedToComposition(coroutineScope)
        onDispose {
            interactionModel.onRemovedFromComposition()
        }
    }
}
