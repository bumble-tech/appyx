package com.bumble.appyx.v2.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bumble.appyx.v2.app.node.child.GenericChildNode
import com.bumble.appyx.v2.app.node.onboarding.OnboardingContainerNode
import com.bumble.appyx.v2.sandbox.ui.AppyxSampleAppTheme
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.RibActivity
import com.bumble.appyx.v2.core.modality.BuildContext

class MainActivity : RibActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppyxSampleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        NodeHost(integrationPoint = integrationPoint) {
                            OnboardingContainerNode(
                                buildContext = it,
                            )
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
    AppyxSampleAppTheme {
        Column {
            OnboardingContainerNode(buildContext = BuildContext.root(null)).Compose()
        }
    }
}
