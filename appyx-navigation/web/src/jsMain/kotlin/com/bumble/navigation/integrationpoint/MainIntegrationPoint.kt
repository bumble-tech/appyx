package com.bumble.navigation.integrationpoint

import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint

class MainIntegrationPoint : IntegrationPoint() {
    override val isChangingConfigurations: Boolean
        get() = false

    override fun onRootFinished() {
    }

    override fun handleUpNavigation() {

    }
}