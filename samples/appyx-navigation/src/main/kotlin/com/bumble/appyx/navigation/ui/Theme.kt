package com.bumble.appyx.navigation.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val DarkColorPalette = darkColorScheme(
    primary = appyx_yellow1,
    secondary = appyx_yellow2,
    background = appyx_dark,
    surface = appyx_dark,
    onPrimary = appyx_bright,
    onSecondary = appyx_bright,
    onBackground = appyx_bright,
    onSurface = appyx_bright,
)

private val LightColorPalette = lightColorScheme(
    primary = appyx_yellow1,
    secondary = appyx_yellow2,
    background = appyx_bright,
    surface = appyx_bright,
    onPrimary = appyx_dark,
    onSecondary = appyx_dark,
    onBackground = appyx_dark,
    onSurface = appyx_dark,
)

@Composable
fun AppyxSampleAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
