package com.bumble.appyx.interactions.core.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Immutable
data class TransitionBounds(
    val density: Density,
    val widthPx: Int,
    val heightPx: Int
) {
    val widthDp: Dp = with (density) { widthPx.toDp() }
    val heightDp: Dp = with (density) { heightPx.toDp() }
}
