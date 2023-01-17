package com.bumble.appyx.interactions.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = appyx_yellow1,
    primaryVariant = appyx_yellow2,
    secondary = appyx_yellow2,
    background = appyx_dark,
    surface = appyx_dark,
    onPrimary = appyx_bright,
    onSecondary = appyx_bright,
    onBackground = appyx_bright,
    onSurface = appyx_bright,
)

private val LightColorPalette = lightColors(
    primary = appyx_yellow1,
    primaryVariant = appyx_yellow2,
    secondary = appyx_yellow2,
    background = appyx_bright,
    surface = appyx_bright,
    onPrimary = appyx_dark,
    onSecondary = appyx_dark,
    onBackground = appyx_dark,
    onSurface = appyx_dark,
)

@Composable
fun AppyxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit)
{
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
