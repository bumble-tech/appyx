package com.bumble.appyx.navigation.integration

import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint

class MainIntegrationPoint : IntegrationPoint() {
    override val isChangingConfigurations: Boolean
        get() = false

    @Suppress("EmptyFunctionBlock")
    override fun onRootFinished() {
    }

    @Suppress("EmptyFunctionBlock")
    override fun handleUpNavigation() {

    }
}
