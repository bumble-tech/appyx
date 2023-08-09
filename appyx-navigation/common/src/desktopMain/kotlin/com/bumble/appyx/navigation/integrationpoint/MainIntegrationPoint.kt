package com.bumble.appyx.navigation.integrationpoint

import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint
import kotlin.system.exitProcess

class MainIntegrationPoint : IntegrationPoint() {
    override val isChangingConfigurations: Boolean
        get() = false

    override fun onRootFinished() {
        exitProcess(0)
    }

    override fun handleUpNavigation() {
        exitProcess(0)
    }
}
