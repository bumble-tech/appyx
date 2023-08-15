package com.bumble.appyx.navigation.integration.permissionrequester

import com.bumble.appyx.navigation.integration.requestcode.RequestCodeBasedEventStream
import com.bumble.appyx.navigation.integration.requestcode.RequestCodeBasedEventStream.RequestCodeBasedEvent
import com.bumble.appyx.navigation.integration.requestcode.RequestCodeClient

interface PermissionRequester :
    RequestCodeBasedEventStream<PermissionRequester.RequestPermissionsEvent> {

    fun checkPermissions(client: RequestCodeClient, permissions: Array<String>) : CheckPermissionsResult

    fun requestPermissions(client: RequestCodeClient, requestCode: Int, permissions: Array<String>)

    data class CheckPermissionsResult(
        val granted: List<String>,
        val notGranted: List<String>,
        val shouldShowRationale: List<String>
    ) {
        val allGranted: Boolean =
            notGranted.isEmpty() && shouldShowRationale.isEmpty()
    }

    sealed class RequestPermissionsEvent : RequestCodeBasedEvent {
        data class Cancelled(
            override val requestCode: Int
        ) : RequestPermissionsEvent()

        data class RequestPermissionsResult(
            override val requestCode: Int,
            val granted: List<String>,
            val denied: List<String>
        ) : RequestPermissionsEvent()
    }
}

