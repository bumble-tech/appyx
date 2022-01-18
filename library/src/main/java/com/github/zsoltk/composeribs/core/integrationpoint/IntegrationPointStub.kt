package com.github.zsoltk.composeribs.core.integrationpoint

import com.github.zsoltk.composeribs.core.integrationpoint.activitystarter.ActivityStarter
import com.github.zsoltk.composeribs.core.integrationpoint.permissionrequester.PermissionRequester

class IntegrationPointStub : IntegrationPoint(savedInstanceState = null) {
    companion object {
        private const val ERROR = "You're accessing a IntegrationPointStub. " +
            "This means you're using a RIB without ever integrating it to a proper IntegrationPoint. " +
            "This is fine during tests with limited scope, but it looks like the code that leads here " +
            "requires interfacing with a valid implementation."
    }

    override val activityStarter: ActivityStarter
        get() = error(ERROR)

    override val permissionRequester: PermissionRequester
        get() = error(ERROR)

    override fun handleUpNavigation() {
        error(ERROR)
    }
}
