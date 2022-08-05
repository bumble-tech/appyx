package com.bumble.appyx.core.integrationpoint.activitystarter

import android.app.Activity
import android.content.Context
import android.content.Intent

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

}
