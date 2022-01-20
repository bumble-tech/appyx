package com.bumble.appyx.v2.core.integrationpoint.permissionrequester

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

interface PermissionRequesterHost {

    fun isGranted(permission: String): Boolean

    fun shouldShowRationale(permission: String): Boolean

    fun requestPermissions(requestCode: Int, permissions: Array<String>)

    class ActivityHost(private val activity: Activity) : PermissionRequesterHost {

        override fun isGranted(permission: String): Boolean =
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

        override fun shouldShowRationale(permission: String): Boolean =
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

        override fun requestPermissions(requestCode: Int, permissions: Array<String>) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }

    }

    class FragmentHost(private val fragment: Fragment) : PermissionRequesterHost {

        override fun isGranted(permission: String): Boolean =
            ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED

        override fun shouldShowRationale(permission: String): Boolean =
            fragment.shouldShowRequestPermissionRationale(permission)

        override fun requestPermissions(requestCode: Int, permissions: Array<String>) {
            fragment.requestPermissions(permissions, requestCode)
        }

    }

}
