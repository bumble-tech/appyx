package com.bumble.appyx.interactions.core.ui

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.CoroutineScope

@Immutable
data class UiContext(val transitionBounds: TransitionBounds, val coroutineScope: CoroutineScope)