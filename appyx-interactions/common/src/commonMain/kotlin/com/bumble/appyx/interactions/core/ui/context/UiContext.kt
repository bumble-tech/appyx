package com.bumble.appyx.interactions.core.ui.context

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.CoroutineScope

@Immutable
data class UiContext(
    val coroutineScope: CoroutineScope,
    val transitionBounds: TransitionBounds,
    val boxScope: BoxScope,
    val clipToBounds: Boolean
)
