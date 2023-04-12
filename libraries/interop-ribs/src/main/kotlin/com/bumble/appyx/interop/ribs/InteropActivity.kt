package com.bumble.appyx.interop.ribs

import android.content.Intent
import android.os.Bundle
import com.badoo.ribs.android.RibActivity
import com.bumble.appyx.navigation.integrationpoint.ActivityIntegrationPoint
import com.bumble.appyx.navigation.integrationpoint.IntegrationPointProvider

abstract class InteropActivity : RibActivity(), IntegrationPointProvider {

    override lateinit var appyxIntegrationPoint: ActivityIntegrationPoint

    protected open fun createAppyxIntegrationPoint(savedInstanceState: Bundle?) =
        ActivityIntegrationPoint(
            activity = this,
            savedInstanceState = savedInstanceState
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        // super.onCreate() creates RIB with AppyxNode inside. It's important to have
        // appyxIntegrationPoint ready before we create a root node
        appyxIntegrationPoint = createAppyxIntegrationPoint(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appyxIntegrationPoint.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        appyxIntegrationPoint.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        appyxIntegrationPoint.onSaveInstanceState(outState)
    }

}
