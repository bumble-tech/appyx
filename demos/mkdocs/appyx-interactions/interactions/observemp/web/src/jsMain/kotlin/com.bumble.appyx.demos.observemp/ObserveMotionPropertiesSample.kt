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
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.interactions.core.ui.property.motionPropertyRenderValue
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
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
            motionController = { SpotlightSliderRotation(it) },
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
    elementUiModel: ElementUiModel<InteractionTarget>,
    isChildMaxSize: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(if (isChildMaxSize) 1f else 0.9f)
            .then(elementUiModel.modifier)
            .background(
                color = when (val target = elementUiModel.element.interactionTarget) {
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
                text = elementUiModel.element.interactionTarget.toString(),
                fontSize = 12.sp,
                color = Color.White
            )
            val dpOffset = motionPropertyRenderValue<PositionOutside.Value, PositionOutside>()?.offset
            if (dpOffset != null) {
                Text(
                    text = "${roundFloatToTwoDecimals(dpOffset.x.value)}.dp",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
            val rotationY = motionPropertyRenderValue<Float, RotationY>()
            if (rotationY != null) {
                Text(
                    text = "${roundFloatToTwoDecimals(rotationY)}°",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

private fun roundFloatToTwoDecimals(float: Float): Double {
    return (float * 100.0).roundToInt() / 100.0
}
