package com.bumble.appyx.core.integrationpoint

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityBoundary
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequestBoundary
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequester

open class ActivityIntegrationPoint(
    private val activity: Activity,
    savedInstanceState: Bundle?,
) : IntegrationPoint(savedInstanceState = savedInstanceState) {
    private val activityBoundary = ActivityBoundary(activity, requestCodeRegistry)
    private val permissionRequestBoundary = PermissionRequestBoundary(activity, requestCodeRegistry)

    override val activityStarter: ActivityStarter
        get() = activityBoundary

    override val permissionRequester: PermissionRequester
        get() = permissionRequestBoundary

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activityBoundary.onActivityResult(requestCode, resultCode, data)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionRequestBoundary.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun handleUpNavigation() {
        if (!activity.onNavigateUp()) {
            activity.onBackPressed()
        }
    }

    override fun onRootFinished() {
        if (!activity.onNavigateUp()) {
            activity.finish()
        }
    }
}
