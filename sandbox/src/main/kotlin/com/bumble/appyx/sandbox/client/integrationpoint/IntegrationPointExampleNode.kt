package com.bumble.appyx.sandbox.client.integrationpoint

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeClient
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.sandbox.client.integrationpoint.StartActivityExample.Companion.StringExtraKey

class IntegrationPointExampleNode(buildContext: BuildContext) : Node(buildContext = buildContext),
    RequestCodeClient {

    private val activityStarter: ActivityStarter get() = integrationPoint.activityStarter
    private var permissionsResultState by mutableStateOf("Press request permissions to check permissions state")
    private var activityResultState by mutableStateOf("Launch activity for result to check result state ")
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>

    override fun onBuilt() {
        super.onBuilt()
        permissionLauncher =
            registerForActivityResult(RequestPermission()) { granted ->
                permissionsResultState = "Permission granted: $granted"
            }
        activityLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    activityResultState =
                        "Activity result: ${result.data?.getStringExtra(StringExtraKey)}"
                }
            }
    }

    @Composable
    override fun View(modifier: Modifier) {

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
                val context = LocalContext.current
                Button(onClick = { launchActivityForResult(context) }) {
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

    private fun launchActivityForResult(context: Context) {
        activityLauncher.launch(Intent(context, StartActivityExample::class.java))
    }

    private fun requestCameraPermissions() {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override val requestCodeClientId: String = IntegrationPointExampleNode::class.qualifiedName!!
}

@Preview
@Composable
fun IntegrationPointExampleNodePreview() {
    IntegrationPointExampleNode(BuildContext.root(null)).Compose()
}
