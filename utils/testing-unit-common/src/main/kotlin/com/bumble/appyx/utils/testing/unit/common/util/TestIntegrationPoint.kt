package com.bumble.appyx.utils.testing.unit.common.util

import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint
import com.bumble.appyx.navigation.navigation.upnavigation.UpNavigationHandler

class TestIntegrationPoint(
    private val upNavigationHandler: UpNavigationHandler,
    override val isChangingConfigurations: Boolean = false
) : IntegrationPoint(), UpNavigationHandler by upNavigationHandler {

    var rootFinished: Boolean = false

    override fun onRootFinished() {
        rootFinished = true
    }
}
