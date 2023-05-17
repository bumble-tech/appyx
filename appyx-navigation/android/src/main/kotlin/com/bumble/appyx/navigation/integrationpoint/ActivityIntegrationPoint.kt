package com.bumble.appyx.navigation.integrationpoint

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import com.bumble.appyx.navigation.integrationpoint.activitystarter.ActivityBoundary
import com.bumble.appyx.navigation.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.navigation.integrationpoint.permissionrequester.PermissionRequestBoundary
import com.bumble.appyx.navigation.integrationpoint.permissionrequester.PermissionRequester

open class ActivityIntegrationPoint(
    private val activity: Activity,
    savedInstanceState: Bundle?,
) : AndroidIntegrationPoint(savedInstanceState = savedInstanceState) {
    private val activityBoundary = ActivityBoundary(activity, requestCodeRegistry)
    private val permissionRequestBoundary = PermissionRequestBoundary(activity, requestCodeRegistry)

    override val isChangingConfigurations: Boolean
        get() = activity.isChangingConfigurations

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
        fun getIntegrationPoint(context: Context): AndroidIntegrationPoint {
            val activity = context.findActivity<Activity>()
            checkNotNull(activity) {
                "Could not find an activity from the context: $context"
            }

            val integrationPointProvider = activity as? IntegrationPointProvider ?: error(
                "Activity ${activity::class.qualifiedName} does not implement IntegrationPointProvider"
            )

            return integrationPointProvider.appyxIntegrationPoint as? AndroidIntegrationPoint
                ?: error(
                    "Activity ${activity::class.qualifiedName} does not provide AndroidIntegrationPoint"
                )
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
