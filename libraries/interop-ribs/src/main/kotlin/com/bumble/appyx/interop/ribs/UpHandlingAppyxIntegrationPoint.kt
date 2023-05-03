package com.bumble.appyx.interop.ribs

import com.badoo.ribs.util.RIBs
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequester

internal class UpHandlingAppyxIntegrationPoint(
    private val delegate: IntegrationPoint,
) : IntegrationPoint(null) {
    private lateinit var upNavigationListener: () -> Unit

    override val activityStarter: ActivityStarter get() = delegate.activityStarter
    override val permissionRequester: PermissionRequester get() = delegate.permissionRequester
    override val isChangingConfigurations: Boolean get() = delegate.isChangingConfigurations
    override fun onRootFinished() = delegate.onRootFinished()

    override fun handleUpNavigation() {
        if (::upNavigationListener.isInitialized) {
            upNavigationListener()
        } else {
            RIBs.errorHandler.handleNonFatalError(
                "Up navigation from initialization sequence is not supported"
            )
        }
    }

    fun setUpNavigationListener(listener: () -> Unit) {
        upNavigationListener = listener
    }

}
