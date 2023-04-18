package com.bumble.appyx.components.spotlight.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.sample.Children
import com.bumble.appyx.interactions.sample.Element
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.interactions.theme.appyx_dark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

fun ComposeContentTestRule.createSpotlight(
    items: List<NavTarget>,
    animationSpec: AnimationSpec<Float> = tween(
        durationMillis = 1000,
        easing = LinearEasing
    )
): Spotlight<NavTarget> {
    val model = SpotlightModel(
        items = items,
        savedStateMap = null
    )

    return Spotlight(
        scope = CoroutineScope(Dispatchers.Unconfined),
        model = model,
        motionController = { SpotlightSlider(it) },
        gestureFactory = { SpotlightSlider.Gestures(it) },
        animationSpec = animationSpec
    ).also { setupSpotlight(it) }
}

fun <InteractionTarget : Any> ComposeContentTestRule.setupSpotlight(
    spotlight: Spotlight<InteractionTarget>,
) {
    setContent {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = appyx_dark
        ) {
            InteractionModelSetup(spotlight)

            SpotlightUi(
                spotlight = spotlight,
                color = Color.Blue
            )
        }
    }
}

@Composable
fun <InteractionTarget : Any> SpotlightUi(
    spotlight: Spotlight<InteractionTarget>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    Children(
        interactionModel = spotlight,
        modifier = modifier
            .padding(
                horizontal = 64.dp,
                vertical = 12.dp
            ),
        element = { elementUiModel ->
            Element(
                color = color,
                elementUiModel = elementUiModel,
                contentDescription =
                "${SPOTLIGHT_EXPERIMENT_TEST_HELPER}_${elementUiModel.element.id}",
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(elementUiModel.element.id) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                spotlight.onDrag(dragAmount, this)
                            },
                            onDragEnd = {
                                Logger.log("drag", "end")
                                spotlight.onDragEnd(
                                    completionThreshold = 0.2f,
                                    completeGestureSpec = spring(),
                                    revertGestureSpec = spring(),
                                )
                            }
                        )
                    }
            )
        }
    )
}

const val SPOTLIGHT_EXPERIMENT_TEST_HELPER = "TheChild"