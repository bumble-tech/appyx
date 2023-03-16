package com.bumble.appyx.interactions.core.ui

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt


fun Dp.toPx(density: Density) = (value * density.density).roundToInt()
