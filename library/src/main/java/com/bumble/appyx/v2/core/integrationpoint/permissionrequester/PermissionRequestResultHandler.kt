package com.bumble.appyx.v2.core.integrationpoint.permissionrequester

interface PermissionRequestResultHandler {

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}
