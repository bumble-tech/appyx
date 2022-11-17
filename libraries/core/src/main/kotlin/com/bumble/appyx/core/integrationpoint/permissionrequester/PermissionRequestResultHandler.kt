package com.bumble.appyx.core.integrationpoint.permissionrequester

@Deprecated("Use AndroidX API")
interface PermissionRequestResultHandler {

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}
