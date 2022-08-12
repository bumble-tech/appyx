package com.bumble.appyx.core.integrationpoint

import androidx.compose.runtime.staticCompositionLocalOf

val LocalIntegrationPoint = staticCompositionLocalOf<IntegrationPoint> {
    error("CompositionLocal LocalIntegrationPoint not present")
}
