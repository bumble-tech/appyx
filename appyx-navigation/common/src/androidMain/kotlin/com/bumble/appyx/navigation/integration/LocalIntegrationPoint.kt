package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.staticCompositionLocalOf
import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint

val LocalIntegrationPoint = staticCompositionLocalOf<IntegrationPoint> {
    error("CompositionLocal LocalIntegrationPoint not present")
}
