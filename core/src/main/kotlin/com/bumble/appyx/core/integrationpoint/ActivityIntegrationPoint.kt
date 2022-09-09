package com.bumble.appyx.core.integrationpoint

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityBoundary
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequestBoundary
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequester
import java.util.WeakHashMap

open class ActivityIntegrationPoint private constructor(
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

    companion object {
        private val integrationPoints = WeakHashMap<Activity, IntegrationPoint>()

        fun createIntegrationPoint(
            activity: Activity,
            savedInstanceState: Bundle?,
        ): ActivityIntegrationPoint =
            ActivityIntegrationPoint(activity, savedInstanceState)
                .also { integrationPoints[activity] = it }

        fun getIntegrationPoint(context: Context): IntegrationPoint {
            val activity = context.findActivity<Activity>()
            check(activity != null) {
                "Could not find an activity from the context: $context"
            }
            val integrationPoint = integrationPoints
                .entries
                .firstOrNull { (key, _) -> key === activity }
                ?.value

            return requireNotNull(integrationPoint) {
                "Unable to find integration point. It was either not created " +
                        "(using ActivityIntegrationPoint.createIntegrationPoint), " +
                        "or the Activity was garbage collected"
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T : Activity> Context.findActivity(): T? {
            return if (this is Activity) {
                this as T?
            } else {
                val contextWrapper = this as ContextWrapper?
                val baseContext = contextWrapper?.baseContext
                requireNotNull(baseContext)
                baseContext.findActivity()
            }
        }
    }
}
