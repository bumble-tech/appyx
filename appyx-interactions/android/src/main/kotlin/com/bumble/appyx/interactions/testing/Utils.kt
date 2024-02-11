package com.bumble.appyx.interactions.testing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.interactions.model.BaseAppyxComponent
import com.bumble.appyx.interactions.ui.helper.AppyxComponentSetup
import com.bumble.appyx.interactions.sample.android.SampleAppyxContainer
import com.bumble.appyx.interactions.theme.appyx_dark
import kotlin.random.Random

fun <InteractionTarget : Any, ModelState : Any> ComposeContentTestRule.setupAppyxComponent(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    fraction: Float = 1.0f,
    clipToBounds: Boolean = false
) {
    setContent {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = appyx_dark
        ) {
            AppyxComponentSetup(appyxComponent)
            TestChildrenUi(
                fraction = fraction,
                appyxComponent = appyxComponent,
                clipToBounds = clipToBounds
            )
        }
    }
}

@Suppress("MagicNumber")
fun randomColor(): Color {
    val random = Random(System.currentTimeMillis())
    return Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))
}


@Composable
private fun <InteractionTarget : Any, ModelState : Any> TestChildrenUi(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    clipToBounds: Boolean,
    fraction: Float = 1.0f,
) {
    BoxWithConstraints {
        val padding = this.maxWidth * (1.0f - fraction) / 2
        SampleAppyxContainer(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = padding)
                .background(
                    color = randomColor()
                ),
            appyxComponent = appyxComponent,
            clipToBounds = clipToBounds,
        ) { element ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "${element.interactionTarget}",
                    modifier = Modifier
                        .align(Alignment.Center),
                    color = Color.White
                )
            }
        }
    }
}
