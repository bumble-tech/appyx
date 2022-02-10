package com.bumble.appyx.v2.sandbox.interop

import android.content.Intent
import android.os.Bundle
import com.badoo.ribs.android.RibActivity
import com.bumble.appyx.v2.core.integrationpoint.ActivityIntegrationPoint

/**
 * Helper class for root [Rib] integration.
 *
 * Also offers base functionality to satisfy dependencies of Android-related functionality
 * down the tree via [integrationPoint]:
 * - [ActivityStarter]
 * - [PermissionRequester]
 *
 * Feel free to not extend this and use your own integration point - in this case,
 * don't forget to take a look here what methods needs to be forwarded to the root Node.
 */
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
        integrationPoint.onSaveInstanceState(outState)
    }

}
