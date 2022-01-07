package com.github.zsoltk.composeribs.client.requestpermissions

import android.Manifest
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
import com.github.zsoltk.composeribs.core.integrationpoint.permissionrequester.PermissionRequester
import com.github.zsoltk.composeribs.core.integrationpoint.requestcode.RequestCodeClient
import com.github.zsoltk.composeribs.core.minimal.reactive.Cancellable
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.node.Node
import java.util.*

class RequestPermissionsNode(buildContext: BuildContext) : Node(buildContext = buildContext),
    RequestCodeClient {

    private val permissionRequester = integrationPoint.permissionRequester
    private var cancellable: Cancellable? = null
    private var permissionsState by mutableStateOf("Press request permissions to check permissions state")

    @Composable
    override fun View() {

        DisposableEffect(key1 = Any()) {
            observeCameraPermissions()
            onDispose {
                cancellable?.cancel()
            }
        }

        Box(
            modifier = Modifier
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
                    text = permissionsState
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { requestCameraPermissions() }) {
                    Text("Request permissions")
                }
            }
        }
    }

    private fun requestCameraPermissions() {
        permissionRequester.requestPermissions(
            client = this,
            requestCode = REQUEST_CODE_CAMERA,
            permissions = arrayOf(Manifest.permission.CAMERA)
        )
    }

    private fun observeCameraPermissions() {
        cancellable = permissionRequester
            .events(this)
            .observe { event ->
                if (event.requestCode == REQUEST_CODE_CAMERA) {
                    when (event) {
                        is PermissionRequester.RequestPermissionsEvent.RequestPermissionsResult -> {
                            permissionsState = "Permission event: $event"
                        }
                        is PermissionRequester.RequestPermissionsEvent.Cancelled -> {
                            permissionsState = "Permission request cancelled"
                        }
                        else -> Unit
                    }
                }
            }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 1
    }

    override val requestCodeClientId: String = UUID.randomUUID().toString()
}

@Preview
@Composable
fun RequestPermissionsNodePreview() {
    RequestPermissionsNode(BuildContext.root(null)).Compose()
}