package com.bumble.appyx.interactions.core.ui.math

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.lerp

fun lerpInt(start: Int, end: Int, progress: Float): Int =
    (start + progress * (end - start)).toInt()

fun lerpFloat(start: Float, end: Float, progress: Float): Float =
    start + progress * (end - start)

fun lerpDpOffset(start: DpOffset, end: DpOffset, progress: Float): DpOffset =
    DpOffset(lerp(start.x, end.x, progress), lerp(start.y, end.y, progress))

fun lerpDp(start: Dp, end: Dp, progress: Float): Dp =
    Dp(lerpFloat(start.value, end.value, progress))
