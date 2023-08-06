package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ScreenSize(val widthDp: Dp, val heightDp: Dp)

@Suppress("CompositionLocalAllowlist")
val LocalScreenSize = compositionLocalOf { ScreenSize(0.dp, 0.dp) }
