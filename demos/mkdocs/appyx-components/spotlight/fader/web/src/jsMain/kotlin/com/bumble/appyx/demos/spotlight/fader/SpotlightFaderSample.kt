package com.bumble.appyx.demos.spotlight.fader

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.next
import com.bumble.appyx.components.spotlight.operation.previous
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.demos.common.AppyxWebSample
import com.bumble.appyx.demos.common.ChildSize
import com.bumble.appyx.demos.common.InteractionTarget
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.model.transition.Operation

@Composable
fun SpotlightFaderSample(
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
            motionController = { SpotlightFader(it) },
            gestureFactory = { SpotlightSlider.Gestures(it) },
        )
    val animationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessVeryLow * 2)
    val actions = mapOf(
        "Prev" to {
            spotlight.previous(
                mode = Operation.Mode.KEYFRAME,
                animationSpec = animationSpec,
            )
        },
        "Next" to {
            spotlight.next(
                mode = Operation.Mode.KEYFRAME,
                animationSpec = animationSpec,
            )
        },
    )
    AppyxWebSample(
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        interactionModel = spotlight.unsafeCast<BaseInteractionModel<InteractionTarget, Any>>(),
        actions = actions,
        childSize = ChildSize.MAX,
        modifier = modifier,
    )
}