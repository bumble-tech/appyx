package com.bumble.appyx.interactions.core.ui.context

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Immutable
data class TransitionBounds(
    val density: Density,
    val widthPx: Int,
    val heightPx: Int,
    val screenWidthPx: Int,
    val screenHeightPx: Int
) {

    val widthDp: Dp = with(density) { widthPx.toDp() }
    val heightDp: Dp = with(density) { heightPx.toDp() }
    val screenWidthDp: Dp = with(density) { screenWidthPx.toDp() }
    val screenHeightDp: Dp = with(density) { screenHeightPx.toDp() }

    companion object {
        val Zero = TransitionBounds(
            density = Density(density = 0f),
            widthPx = 0,
            heightPx = 0,
            screenWidthPx = 0,
            screenHeightPx = 0
        )
    }
}
