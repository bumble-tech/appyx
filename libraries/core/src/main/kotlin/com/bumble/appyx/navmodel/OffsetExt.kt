package com.bumble.appyx.navmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

internal fun Offset.toIntOffset(density: Float) = IntOffset(
    x = (x * density).roundToInt(),
    y = (y * density).roundToInt()
)
