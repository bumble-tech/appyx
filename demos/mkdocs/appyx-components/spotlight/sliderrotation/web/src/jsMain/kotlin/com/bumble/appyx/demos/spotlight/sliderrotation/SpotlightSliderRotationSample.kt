package com.bumble.appyx.demos.spotlight.sliderrotation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.next
import com.bumble.appyx.components.spotlight.operation.previous
import com.bumble.appyx.components.spotlight.ui.sliderrotation.SpotlightSliderRotation
import com.bumble.appyx.demos.common.AppyxWebSample
import com.bumble.appyx.demos.common.InteractionTarget
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory

@Composable
fun SpotlightSliderRotationSample(
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val model = remember {
        SpotlightModel<InteractionTarget>(
            items = List(7) { InteractionTarget.Element(it) },
            initialActiveIndex = 0f,
            savedStateMap = null
        )
    }
    val spotlight =
        Spotlight(
            scope = coroutineScope,
            model = model,
            motionController = { SpotlightSliderRotation(it) },
            gestureFactory = { GestureFactory.Noop() }
        )
    val actions = mapOf(
        "Prev" to { spotlight.previous() },
        "Next" to { spotlight.next() },
    )
    AppyxWebSample(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        interactionModel = spotlight.unsafeCast<BaseInteractionModel<InteractionTarget, Any>>(),
        actions = actions,
        modifier = modifier,
    )
}