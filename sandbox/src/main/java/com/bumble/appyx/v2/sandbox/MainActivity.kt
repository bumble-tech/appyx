package com.bumble.appyx.v2.sandbox

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.RibActivity
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode
import com.bumble.appyx.v2.sandbox.ui.AppyxSandboxTheme

class MainActivity : RibActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppyxSandboxTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        NodeHost(integrationPoint = integrationPoint) {
                            ContainerNode(buildContext = it)
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppyxSandboxTheme {
        Column {
            ContainerNode(buildContext = BuildContext.root(null)).Compose()
        }
    }
}
