package com.bumble.appyx.sample.navigtion.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Density
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.bumble.appyx.app.ui.AppyxSampleAppTheme
import org.junit.Rule
import org.junit.Test

class ScreenshotTests {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "Theme.Appyx.NoActionBar"
    )

    @Test
    fun launchView() {
        paparazzi.snapshot("GoogleRoute") {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalDensity provides Density(
                    density = LocalDensity.current.density
                )
            ) {
                AppyxSampleAppTheme {
                    GoogleRoute {}
                }
            }
        }
    }
}
