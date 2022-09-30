package com.bumble.appyx.sandbox

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.portal.PortalNode
import com.bumble.appyx.sandbox.client.container.ContainerBuilder
import com.bumble.appyx.sandbox.client.container.ContainerNode
import com.bumble.appyx.sandbox.ui.AppyxSandboxTheme

class MainActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppyxSandboxTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        NodeHost(integrationPoint = integrationPoint) {
                            PortalNode(buildContext = it) { buildContext, portalClientFactory ->
                                ContainerNode(
                                    portalClientFactory = portalClientFactory,
                                    buildContext = buildContext,
                                )
                            }
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
            ContainerBuilder().build(buildContext = BuildContext.root(null)).Compose()
        }
    }
}
