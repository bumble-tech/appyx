package com.bumble.appyx.components.experimental.puzzle15

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.components.experimental.puzzle15.ui.Puzzle15Ui
import com.bumble.appyx.interactions.sample.colors
import com.bumble.appyx.interactions.theme.appyx_dark
import kotlin.math.roundToInt

@Composable
fun Puzzle15() {
    Puzzle15Ui(
        screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
        screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
        colors = colors,
        modifier = Modifier.fillMaxSize().background(appyx_dark),
    )
}
