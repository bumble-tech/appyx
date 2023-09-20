package com.bumble.appyx.navigation.integration.activitystarter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

interface ActivityStarterHost {

    val context: Context

    fun startActivity(intent: Intent)

    fun startActivityForResult(intent: Intent, requestCode: Int)

    class ActivityHost(private val activity: Activity) : ActivityStarterHost {
        override val context: Context
            get() = activity

        override fun startActivity(intent: Intent) {
            activity.startActivity(intent)
        }

        override fun startActivityForResult(intent: Intent, requestCode: Int) {
            activity.startActivityForResult(intent, requestCode)
        }
    }

    class FragmentHost(private val fragment: Fragment) : ActivityStarterHost {
        override val context: Context
            get() = fragment.requireContext()

        override fun startActivity(intent: Intent) {
            fragment.startActivity(intent)
        }

        override fun startActivityForResult(intent: Intent, requestCode: Int) {
            fragment.startActivityForResult(intent, requestCode)
        }
    }

}
