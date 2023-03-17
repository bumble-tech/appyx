package com.bumble.appyx.interactions.core.ui.context

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

@Immutable
data class TransitionBounds(
    val density: Density,
    val widthPx: Int,
    val heightPx: Int,
    val screenWidthPx: Int,
    val screenHeightPx: Int,
    val containerBoundsInRoot: Rect
) {

    val widthDp: Dp = with(density) { widthPx.toDp() }
    val heightDp: Dp = with(density) { heightPx.toDp() }
    val screenWidthDp: Dp = with(density) { screenWidthPx.toDp() }
    val screenHeightDp: Dp = with(density) { screenHeightPx.toDp() }
    val containerOffsetXPx = with(density) { containerBoundsInRoot.bottomLeft.x.roundToInt() }
    val containerOffsetXDp = with(density) { containerBoundsInRoot.bottomLeft.x.toDp() }
    val containerOffsetYDp = with(density) { containerBoundsInRoot.topLeft.y.toDp() }
    val containerOffsetYPx = with(density) { containerBoundsInRoot.topLeft.y.roundToInt() }
}

val zeroSizeTransitionBounds  = TransitionBounds(Density(0f), 0, 0, 0, 0, Rect(0f, 0f, 0f, 0f))
