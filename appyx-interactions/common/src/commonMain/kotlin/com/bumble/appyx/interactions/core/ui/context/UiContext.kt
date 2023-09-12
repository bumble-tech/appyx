package com.bumble.appyx.interactions.core.ui.context

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.CoroutineScope

@Immutable
data class UiContext(
    val coroutineScope: CoroutineScope,
    val clipToBounds: Boolean
)
