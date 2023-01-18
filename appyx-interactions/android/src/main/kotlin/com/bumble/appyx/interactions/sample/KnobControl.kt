package com.bumble.appyx.interactions.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.theme.appyx_yellow1
import kotlin.math.roundToInt


@ExperimentalMaterialApi
@Composable
fun KnobControl(
    onValueChange: (Float) -> Unit
) {
    val knobSizeDp = 48.dp
    val knobSizePx = with(LocalDensity.current) { knobSizeDp.toPx() }
    val swipeableState = rememberSwipeableState(0)

    var containerBounds by remember { mutableStateOf(IntSize(0, 0)) }
    val anchors = remember(containerBounds) {
        val width = containerBounds.width.toFloat() - knobSizePx
        mapOf(0f to 0, (if (width > 0) width else 1f) to 1)
    }

    LaunchedEffect(swipeableState.progress) {
        val progress = swipeableState.progress
        val normalisedProgress = progress.from + (progress.to - progress.from) * progress.fraction
        Logger.log("swipeable", swipeableState.progress.toString())
        Logger.log("swipeable", normalisedProgress.toString())

        onValueChange(normalisedProgress)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { containerBounds = it }
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal
            )
            .drawBehind {
                drawLine(
                    color = appyx_yellow1,
                    alpha = 0.5f,
                    strokeWidth = 6f,
                    start = Offset(x = knobSizePx / 2, y = size.height / 2),
                    end = Offset(x = size.width - knobSizePx / 2, y = size.height / 2)
                )
            }
    ) {
        Box(
            Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .size(knobSizeDp)
                .padding(knobSizeDp / 6)
                .clip(CircleShape)
                .background(appyx_yellow1)
        )
    }
}
