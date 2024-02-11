package com.bumble.appyx.demos.observemp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.first
import com.bumble.appyx.components.spotlight.operation.last
import com.bumble.appyx.components.spotlight.operation.next
import com.bumble.appyx.components.spotlight.operation.previous
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.components.spotlight.ui.sliderrotation.SpotlightSliderRotation
import com.bumble.appyx.demos.common.AppyxWebSample
import com.bumble.appyx.demos.common.InteractionTarget
import com.bumble.appyx.demos.common.colors
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.ui.property.impl.RotationY
import com.bumble.appyx.interactions.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.ui.property.motionPropertyRenderValue
import kotlin.math.roundToInt

@Composable
fun ObserveMotionPropertiesSample(
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val model = remember {
        SpotlightModel<InteractionTarget>(
            items = List(7) { InteractionTarget.Element(it) },
            initialActiveIndex = 1f,
            savedStateMap = null
        )
    }
    val spotlight = remember {
        Spotlight(
            scope = coroutineScope,
            model = model,
            visualisation = { SpotlightSliderRotation(it, model.currentState) },
            gestureFactory = { SpotlightSlider.Gestures(it) }
        )
    }
    val actions = mapOf(
        "First" to { spotlight.first() },
        "Prev" to { spotlight.previous() },
        "Next" to { spotlight.next() },
        "Last" to { spotlight.last() },

        )
    AppyxWebSample(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        appyxComponent = spotlight,
        actions = actions,
        modifier = modifier,
    ) {
        ModalUi(it, false)
    }
}

@Composable
fun <InteractionTarget : Any> ModalUi(
    element: Element<InteractionTarget>,
    isChildMaxSize: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(if (isChildMaxSize) 1f else 0.9f)
            .background(
                color = when (val target = element.interactionTarget) {
                    is com.bumble.appyx.demos.common.InteractionTarget.Element -> colors.getOrElse(
                        target.idx % colors.size
                    ) { Color.Cyan }

                    else -> {
                        Color.Cyan
                    }
                },
                shape = RoundedCornerShape(if (isChildMaxSize) 0 else 8)
            )
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = element.interactionTarget.toString(),
                fontSize = 12.sp,
                color = Color.White
            )
            val alignment =
                motionPropertyRenderValue<PositionAlignment.Value, PositionAlignment>()
            if (alignment != null) {
                val offsetPercentage = roundFloatToTwoDecimals(
                    alignment.outsideAlignment.horizontalBias * 100
                )

                Text(
                    text = "Offset:\n$offsetPercentage%",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
            val rotationY = motionPropertyRenderValue<Float, RotationY>()
            if (rotationY != null) {
                Text(
                    text = "Rotation:\n${roundFloatToTwoDecimals(rotationY)}°",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
    }
}

private fun roundFloatToTwoDecimals(float: Float): Double {
    return (float * 100.0).roundToInt() / 100.0
}
