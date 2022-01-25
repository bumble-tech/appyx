package com.bumble.appyx.v2.app.node.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.v2.app.composable.Page
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.modality.BuildContext.Companion.root
import com.bumble.appyx.v2.core.node.Node

@ExperimentalUnitApi
class OnboardingScreenNode(
    buildContext: BuildContext,
    private val screenData: ScreenData.PlainWithImage,
) : Node(
    buildContext = buildContext
) {

    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = screenData.title,
            body = screenData.body
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {

            }
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
fun OnboardingScreenParentNodePreview() {
    Box(Modifier.fillMaxSize()) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            OnboardingScreenNode(
                root(null),
                ScreenData.PlainWithImage(
                    imageRes = 0,
                    title = "Title",
                    body = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna ali- quam erat volutpat."
                )
            )
        }
    }
}
