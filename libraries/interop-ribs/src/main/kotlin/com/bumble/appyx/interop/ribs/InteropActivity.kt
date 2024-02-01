package com.bumble.appyx.interop.ribs

import android.content.Intent
import android.os.Bundle
import com.badoo.ribs.android.RibActivity
import com.bumble.appyx.core.integrationpoint.ActivityIntegrationPoint
import com.bumble.appyx.core.integrationpoint.IntegrationPointProvider

abstract class InteropActivity : RibActivity(), IntegrationPointProvider {

    override lateinit var appyxV1IntegrationPoint: ActivityIntegrationPoint

    protected open fun createAppyxIntegrationPoint(savedInstanceState: Bundle?) =
        ActivityIntegrationPoint(
            activity = this,
            savedInstanceState = savedInstanceState
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        // super.onCreate() creates RIB with AppyxNode inside. It's important to have
        // appyxV1IntegrationPoint ready before we create a root node
        appyxV1IntegrationPoint = createAppyxIntegrationPoint(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appyxV1IntegrationPoint.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        appyxV1IntegrationPoint.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        appyxV1IntegrationPoint.onSaveInstanceState(outState)
    }

}
