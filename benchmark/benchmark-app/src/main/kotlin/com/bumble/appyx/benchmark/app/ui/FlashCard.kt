package com.bumble.appyx.benchmark.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import com.bumble.appyx.interactions.core.ui.math.smoothstep
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.motionPropertyRenderValue
import kotlin.math.PI
import kotlin.math.sin


@Suppress("MagicNumber")
@Composable
fun FlashCard(
    front: @Composable (modifier: Modifier) -> Unit,
    back: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier,
    flash: Color = Color.Unspecified
) {
    val rotation = (motionPropertyRenderValue<Float, RotationY>() ?: 0f) % 360f
    val contentDrawModifier = if (flash == Color.Unspecified) Modifier else remember(rotation) {
        val edgeFlash = 1 - smoothstep(0f, 0.15f, (1 - sin(rotation / 180.0 * PI)).toFloat())
        if (edgeFlash == 0f) {
            Modifier
        } else {
            Modifier.drawWithContent {
                drawContent()
                drawRect(
                    brush = SolidColor(flash),
                    size = size,
                    alpha = edgeFlash,
                )
            }
        }
    }

    if (rotation in 90f..270f) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = -1f
                }
        ) {
            back(contentDrawModifier)
        }
    } else {
        front(contentDrawModifier)
    }
}
