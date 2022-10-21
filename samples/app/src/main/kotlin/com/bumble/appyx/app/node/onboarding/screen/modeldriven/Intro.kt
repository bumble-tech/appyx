package com.bumble.appyx.app.node.onboarding.screen.modeldriven

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.R
import com.bumble.appyx.app.composable.Page
import com.bumble.appyx.app.ui.AppyxSampleAppTheme
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.modality.BuildContext.Companion.root
import com.bumble.appyx.core.node.Node

@ExperimentalUnitApi
class Intro(
    buildContext: BuildContext,
) : Node(
    buildContext = buildContext
) {

    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = "Hi there!",
            body = "Appyx is a model-driven navigation library built with love on top of Jetpack Compose."
        ) {
            val image: Painter = painterResource(id = R.drawable.appyx)
            Image(
                painter = image,
                contentDescription = "logo",
                modifier = Modifier
                    .fillMaxSize(0.65f)
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
fun IntroScreenPreview() {
    AppyxSampleAppTheme(darkTheme = false) {
        PreviewContent()
    }
}

@Preview
@Composable
@ExperimentalUnitApi
fun IntroScreenPreviewDark() {
    AppyxSampleAppTheme(darkTheme = true) {
        PreviewContent()
    }
}

@Composable
@ExperimentalUnitApi
private fun PreviewContent() {
    Surface(color = MaterialTheme.colors.background) {
        Box(Modifier.fillMaxSize()) {
            NodeHost(integrationPoint = IntegrationPointStub()) {
                Intro(
                    root(null)
                )
            }
        }
    }
}
