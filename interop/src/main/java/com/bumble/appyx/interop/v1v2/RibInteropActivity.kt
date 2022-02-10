package com.bumble.appyx.interop.v1v2

import android.content.Intent
import android.os.Bundle
import com.badoo.ribs.android.RibActivity
import com.bumble.appyx.v2.core.integrationpoint.ActivityIntegrationPoint

abstract class RibInteropActivity : RibActivity() {

    lateinit var integrationPointV2: ActivityIntegrationPoint
        protected set

    protected open fun createIntegrationPointV2(savedInstanceState: Bundle?): ActivityIntegrationPoint =
        ActivityIntegrationPoint(
            activity = this,
            savedInstanceState = savedInstanceState
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        integrationPointV2 = createIntegrationPointV2(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        integrationPointV2.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        integrationPointV2.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        integrationPointV2.onSaveInstanceState(outState)
    }

}
