package com.bumble.appyx.interactions.sample.testdrive.helper

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.interactions.setupSpotlight
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.ui.slider.SpotlightSlider
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
