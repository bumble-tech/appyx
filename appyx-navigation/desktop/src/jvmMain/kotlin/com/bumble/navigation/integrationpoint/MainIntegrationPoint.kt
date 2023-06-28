package com.bumble.navigation.integrationpoint

import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint
import kotlin.system.exitProcess

class MainIntegrationPoint(
    private val onNavigateUp: () -> Boolean
) : IntegrationPoint() {
    override val isChangingConfigurations: Boolean
        get() = false

    override fun onRootFinished() {
        if (!onNavigateUp()) {
            exitProcess(0)
        }
    }

    override fun handleUpNavigation() {
        if (!onNavigateUp()) {
            exitProcess(0)
        }
    }
}
