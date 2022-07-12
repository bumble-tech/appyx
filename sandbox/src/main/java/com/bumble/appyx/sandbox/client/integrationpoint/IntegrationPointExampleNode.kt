package com.bumble.appyx.sandbox.client.integrationpoint

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequester
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeClient
import com.bumble.appyx.core.minimal.reactive.Cancellable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.sandbox.client.integrationpoint.StartActivityExample.Companion.StringExtraKey
import java.util.UUID

class IntegrationPointExampleNode(buildContext: BuildContext) : Node(buildContext = buildContext),
    RequestCodeClient {

    private val permissionRequester: PermissionRequester get() = integrationPoint.permissionRequester
    private val activityStarter: ActivityStarter get() = integrationPoint.activityStarter
    private var permissionsResultCancellable: Cancellable? = null
    private var activityResultsCancellable: Cancellable? = null
    private var permissionsResultState by mutableStateOf("Press request permissions to check permissions state")
    private var activityResultState by mutableStateOf("Launch activity for result to check result state ")

    @Composable
    override fun View(modifier: Modifier) {

        DisposableEffect(key1 = Any()) {
            observeCameraPermissions()
            observeActivityResult()
            onDispose {
                permissionsResultCancellable?.cancel()
                activityResultsCancellable?.cancel()
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.Gray)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .wrapContentSize()
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = permissionsResultState
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { requestCameraPermissions() }) {
                    Text("Request permissions")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { launchActivity() }) {
                    Text("Launch activity")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { launchActivityForResult() }) {
                    Text("Launch activity for result")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = activityResultState
                )
            }
        }
    }

    private fun launchActivity() {
        activityStarter.startActivity {
            Intent(this, StartActivityExample::class.java)
        }
    }

    private fun launchActivityForResult() {
        activityStarter.startActivityForResult(this, StartActivityForResultCode) {
            Intent(this, StartActivityExample::class.java)
        }
    }

    private fun requestCameraPermissions() {
        permissionRequester.requestPermissions(
            client = this,
            requestCode = RequestCameraCode,
            permissions = arrayOf(Manifest.permission.CAMERA)
        )
    }

    private fun observeCameraPermissions() {
        permissionsResultCancellable = permissionRequester
            .events(this)
            .observe { event ->
                if (event.requestCode == RequestCameraCode) {
                    when (event) {
                        is PermissionRequester.RequestPermissionsEvent.RequestPermissionsResult -> {
                            permissionsResultState = "Permission event: $event"
                        }
                        is PermissionRequester.RequestPermissionsEvent.Cancelled -> {
                            permissionsResultState = "Permission request cancelled"
                        }
                        else -> Unit
                    }
                }
            }
    }

    private fun observeActivityResult() {
        activityResultsCancellable = activityStarter
            .events(this)
            .observe { event ->
                if (event.requestCode == StartActivityForResultCode && event.resultCode == Activity.RESULT_OK && event.data != null) {
                    val result = event.data?.getStringExtra(StringExtraKey) ?: ""
                    activityResultState = "Activity result: $result"
                }
            }
    }

    companion object {
        private const val RequestCameraCode = 1
        private const val StartActivityForResultCode = 2
    }

    override val requestCodeClientId: String = UUID.randomUUID().toString()
}

@Preview
@Composable
fun IntegrationPointExampleNodePreview() {
    IntegrationPointExampleNode(BuildContext.root(null)).Compose()
}
