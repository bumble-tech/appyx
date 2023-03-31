package com.bumble.appyx.testing.unit.common.util

import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequester
import com.bumble.appyx.core.navigation.upnavigation.UpNavigationHandler

class TestIntegrationPoint(
    private val upNavigationHandler: UpNavigationHandler,
    override val isChangingConfigurations: Boolean = false
) : IntegrationPoint(savedInstanceState = null), UpNavigationHandler by upNavigationHandler {

    var rootFinished: Boolean = false

    override val activityStarter: ActivityStarter
        get() = TODO()

    override val permissionRequester: PermissionRequester
        get() = TODO()

    override fun onRootFinished() {
        rootFinished = true
    }
}
