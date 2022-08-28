package com.bumble.appyx.interop.ribs

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.viewinterop.AndroidView
import com.badoo.ribs.android.RibActivity
import com.bumble.appyx.core.integrationpoint.ActivityIntegrationPoint
import com.bumble.appyx.core.integrationpoint.LocalIntegrationPoint

abstract class InteropActivity : RibActivity() {

    private lateinit var integrationPointAppyx: ActivityIntegrationPoint

    protected open fun createIntegrationPointV2(savedInstanceState: Bundle?): ActivityIntegrationPoint =
        ActivityIntegrationPoint(
            activity = this,
            savedInstanceState = savedInstanceState
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        integrationPointAppyx = createIntegrationPointV2(savedInstanceState)

        setContent {
            CompositionLocalProvider(LocalIntegrationPoint provides integrationPointAppyx) {
                AndroidView(factory = { rootViewGroup })
            }
        }
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
