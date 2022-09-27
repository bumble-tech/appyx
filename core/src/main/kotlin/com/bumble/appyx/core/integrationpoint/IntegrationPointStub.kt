package com.bumble.appyx.core.integrationpoint

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultRegistry
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequester

class IntegrationPointStub : IntegrationPoint(savedInstanceState = null) {
    companion object {
        private const val ERROR = "You're accessing an IntegrationPointStub. " +
            "This means you're using a Node without ever integrating it to a proper IntegrationPoint. " +
            "This is fine during tests with limited scope, but it looks like the code that leads here " +
            "requires interfacing with a valid implementation. " +
            "You may be attempting to access the IntegrationPoint before it is attached to the Node."
    }

    override val activityStarter: ActivityStarter
        get() = error(ERROR)

    override val permissionRequester: PermissionRequester
        get() = error(ERROR)

    override val activityResultCaller: ActivityResultCaller
        get() = error(ERROR)

    override val activityResultRegistry: ActivityResultRegistry
        get() = error(ERROR)

    override fun handleUpNavigation() {
        error(ERROR)
    }

    override fun onRootFinished() {
        error(ERROR)
    }
}
