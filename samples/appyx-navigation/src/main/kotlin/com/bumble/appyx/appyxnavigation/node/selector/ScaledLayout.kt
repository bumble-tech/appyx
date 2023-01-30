package com.bumble.appyx.appyxnavigation.node.selector

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import kotlin.math.roundToInt

@Composable
internal fun ScaledLayout(
    modifier: Modifier = Modifier,
    layoutScale: Float = 4f,
    content: @Composable @UiComposable () -> Unit
) {
    val renderingScale = 1 / layoutScale
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val scaledUpConstraints = constraints.copy(
            maxWidth = (constraints.maxWidth * layoutScale).roundToInt(),
            maxHeight = (constraints.maxHeight * layoutScale).roundToInt()
        )
        val placeables = measurables.map { measurable ->
            measurable.measure(scaledUpConstraints)
        }

        layout(
            constraints.maxWidth,
            constraints.maxHeight
        ) {
            placeables.forEach { placeable ->
                placeable.placeRelativeWithLayer(
                    x = 0,
                    y = 0
                ) {
                    scaleX = renderingScale
                    scaleY = renderingScale
                    transformOrigin = TransformOrigin(0f, 0f)
                }
            }
        }
    }
}
