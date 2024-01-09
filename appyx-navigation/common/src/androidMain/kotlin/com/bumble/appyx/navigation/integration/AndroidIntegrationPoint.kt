package com.bumble.appyx.navigation.integration

import android.os.Bundle
import androidx.compose.runtime.Stable
import com.bumble.appyx.navigation.integration.activitystarter.ActivityStarter
import com.bumble.appyx.navigation.integration.permissionrequester.PermissionRequester
import com.bumble.appyx.navigation.integration.requestcode.RequestCodeRegistry

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
