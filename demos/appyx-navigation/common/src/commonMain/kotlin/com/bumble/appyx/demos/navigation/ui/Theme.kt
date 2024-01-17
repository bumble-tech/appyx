package com.bumble.appyx.demos.navigation.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val DarkColorPalette = darkColorScheme()
private val LightColorPalette = lightColorScheme()

@Composable
fun AppyxSampleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeTypography: Typography = typography,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = themeTypography,
        shapes = shapes,
        content = content
    )
}
