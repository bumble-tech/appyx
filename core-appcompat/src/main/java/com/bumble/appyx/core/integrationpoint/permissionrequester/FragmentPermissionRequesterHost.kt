package com.bumble.appyx.core.integrationpoint.permissionrequester

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class FragmentPermissionRequesterHost(private val fragment: Fragment) : PermissionRequesterHost {

    override fun isGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED

    override fun shouldShowRationale(permission: String): Boolean =
        fragment.shouldShowRequestPermissionRationale(permission)

    override fun requestPermissions(requestCode: Int, permissions: Array<String>) {
        fragment.requestPermissions(permissions, requestCode)
    }

}
