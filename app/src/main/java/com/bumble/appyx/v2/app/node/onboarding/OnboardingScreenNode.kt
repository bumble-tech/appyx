package com.bumble.appyx.v2.app.node.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.modality.BuildContext.Companion.root
import com.bumble.appyx.v2.core.node.Node

class OnboardingScreenNode(
    buildContext: BuildContext,
    private val screenData: ScreenData
) : Node(
    buildContext = buildContext
) {

    @Composable
    override fun View(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            ) {
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(0.35f)
            ) {
                Text(
                    text = screenData.title,
                    style = MaterialTheme.typography.h4
                )
                Text(
                    text = screenData.body,
                    style = MaterialTheme.typography.body1
                )
            }

        }
    }
}

@Preview
@Composable
fun OnboardingScreenNodePreview() {
    Box(Modifier.fillMaxSize()) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            OnboardingScreenNode(
                root(null),
                ScreenData(
                    title = "Title",
                    body = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna ali- quam erat volutpat."
                )
            )
        }
    }
}
