package com.bumble.appyx.navigation.integrationpoint

import android.os.Bundle
import androidx.compose.runtime.Stable
import com.bumble.appyx.navigation.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.navigation.integrationpoint.permissionrequester.PermissionRequester
import com.bumble.appyx.navigation.integrationpoint.requestcode.RequestCodeRegistry

@Stable
abstract class AndroidIntegrationPoint(
    protected val savedInstanceState: Bundle?
) : IntegrationPoint() {

    protected val requestCodeRegistry =
        RequestCodeRegistry(
            savedInstanceState
        )

    abstract val activityStarter: ActivityStarter

    abstract val permissionRequester: PermissionRequester

    fun onSaveInstanceState(outState: Bundle) {
        requestCodeRegistry.onSaveInstanceState(outState)
    }
}
