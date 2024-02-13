package com.bumble.appyx.interactions.ui

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

internal fun Dp.toPx(density: Density) = with(density) { roundToPx() }
