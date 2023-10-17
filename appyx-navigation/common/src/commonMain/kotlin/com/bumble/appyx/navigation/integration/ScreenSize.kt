package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.integration.ScreenSize.WindowSizeClass.COMPACT
import com.bumble.appyx.navigation.integration.ScreenSize.WindowSizeClass.EXPANDED
import com.bumble.appyx.navigation.integration.ScreenSize.WindowSizeClass.MEDIUM

data class ScreenSize(val widthDp: Dp, val heightDp: Dp) {

    enum class WindowSizeClass {
        COMPACT, MEDIUM, EXPANDED
    }

    val windowSizeClass: WindowSizeClass =
        when {
            widthDp < 600.dp -> COMPACT
            widthDp < 840.dp -> MEDIUM
            else -> EXPANDED
        }
}

@Suppress("CompositionLocalAllowlist")
val LocalScreenSize = compositionLocalOf { ScreenSize(0.dp, 0.dp) }
