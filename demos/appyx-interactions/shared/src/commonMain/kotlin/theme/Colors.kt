package theme

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

val appyx_yellow1 = Color(0xFFFFC629)
val appyx_yellow2 = Color(0xFFFFE54A)
val appyx_dark = Color(0xFF1F2126)
val appyx_bright = Color(0xFFFFFFFF)

val DarkColorPalette = darkColors(
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