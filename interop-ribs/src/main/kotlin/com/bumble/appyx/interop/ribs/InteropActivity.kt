package com.bumble.appyx.interop.ribs

import android.content.Intent
import android.os.Bundle
import com.badoo.ribs.android.RibActivity
import com.bumble.appyx.core.integrationpoint.ActivityIntegrationPoint

abstract class InteropActivity : RibActivity() {

    private lateinit var integrationPointAppyx: ActivityIntegrationPoint

    protected open fun createIntegrationPointV2(savedInstanceState: Bundle?): ActivityIntegrationPoint =
        ActivityIntegrationPoint.createIntegrationPoint(
            activity = this,
            savedInstanceState = savedInstanceState
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        integrationPointAppyx = createIntegrationPointV2(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        integrationPointAppyx.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        integrationPointAppyx.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        integrationPointAppyx.onSaveInstanceState(outState)
    }

}
