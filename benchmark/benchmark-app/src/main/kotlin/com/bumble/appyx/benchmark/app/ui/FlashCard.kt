package com.bumble.appyx.benchmark.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.bumble.appyx.interactions.ui.property.impl.RotationY
import com.bumble.appyx.interactions.ui.property.motionPropertyRenderValue


@Suppress("MagicNumber")
@Composable
fun FlashCard(
    front: @Composable (modifier: Modifier) -> Unit,
    back: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation = (motionPropertyRenderValue<Float, RotationY>() ?: 0f) % 360f

    if (rotation in 90f..270f) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = -1f
                }
        ) {
            back(Modifier)
        }
    } else {
        front(modifier)
    }
}
