package com.bumble.appyx.interactions.core.ui.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.bumble.appyx.interactions.core.model.InteractionModel

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
