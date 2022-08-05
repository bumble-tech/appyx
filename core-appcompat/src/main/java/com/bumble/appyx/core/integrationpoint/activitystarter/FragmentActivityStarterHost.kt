package com.bumble.appyx.core.integrationpoint.activitystarter

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

class FragmentActivityStarterHost(private val fragment: Fragment) : ActivityStarterHost {
    override val context: Context
        get() = fragment.requireContext()

    override fun startActivity(intent: Intent) {
        fragment.startActivity(intent)
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        fragment.startActivityForResult(intent, requestCode)
    }
}
