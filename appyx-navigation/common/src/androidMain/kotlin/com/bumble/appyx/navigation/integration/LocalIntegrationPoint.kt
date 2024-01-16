package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.staticCompositionLocalOf

val LocalIntegrationPoint = staticCompositionLocalOf<IntegrationPoint> {
    error("CompositionLocal LocalIntegrationPoint not present")
}
