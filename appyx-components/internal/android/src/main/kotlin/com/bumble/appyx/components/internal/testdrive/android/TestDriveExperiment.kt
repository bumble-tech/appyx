package com.bumble.appyx.components.internal.testdrive.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.components.internal.testdrive.TestDriveExperiment
import com.bumble.appyx.interactions.sample.InteractionTarget
import com.bumble.appyx.interactions.sample.colors
import com.bumble.appyx.interactions.theme.appyx_dark
import kotlin.math.roundToInt

@Composable
fun TestDriveExperiment() {
    TestDriveExperiment(
        screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
        screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
        colors = colors,
        element = InteractionTarget.Child1,
        modifier = Modifier.fillMaxSize().background(appyx_dark),
    )
}
