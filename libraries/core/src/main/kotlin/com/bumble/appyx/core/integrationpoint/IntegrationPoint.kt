package com.bumble.appyx.core.integrationpoint

import android.os.Bundle
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultRegistry
import androidx.compose.runtime.Stable
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequester
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeRegistry
import com.bumble.appyx.core.navigation.upnavigation.UpNavigationHandler

@Stable
abstract class IntegrationPoint(
    protected val savedInstanceState: Bundle?
) : UpNavigationHandler {

    protected val requestCodeRegistry = RequestCodeRegistry(savedInstanceState)

    @Deprecated("Use AndroidX API")
    abstract val activityStarter: ActivityStarter

    @Deprecated("Use AndroidX API")
    abstract val permissionRequester: PermissionRequester

    abstract val activityResultCaller: ActivityResultCaller

    abstract val activityResultRegistry: ActivityResultRegistry

    fun onSaveInstanceState(outState: Bundle) {
        requestCodeRegistry.onSaveInstanceState(outState)
    }

    abstract fun onRootFinished()
}
