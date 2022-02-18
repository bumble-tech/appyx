package com.bumble.appyx.v2.core.testutils

import com.bumble.appyx.v2.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.v2.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.v2.core.integrationpoint.permissionrequester.PermissionRequester
import com.bumble.appyx.v2.core.routing.upnavigation.UpNavigationHandler

class TestIntegrationPoint(
    private val upNavigationHandler: UpNavigationHandler
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
