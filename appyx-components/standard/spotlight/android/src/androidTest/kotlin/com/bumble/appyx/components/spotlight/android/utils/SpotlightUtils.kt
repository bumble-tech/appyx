package com.bumble.appyx.components.spotlight.android.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.ui.helper.AppyxComponentSetup
import com.bumble.appyx.interactions.utils.testing.TestTarget
import com.bumble.appyx.interactions.utils.ui.Element
import com.bumble.appyx.interactions.utils.ui.SampleAppyxContainer
import com.bumble.appyx.interactions.utils.ui.theme.appyx_dark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

fun ComposeContentTestRule.createSpotlight(
    items: List<TestTarget>,
    animationSpec: AnimationSpec<Float> = tween(
        durationMillis = 1000,
        easing = LinearEasing
    )
): Spotlight<TestTarget> {
    val model = SpotlightModel(
        items = items,
        savedStateMap = null
    )

    return Spotlight(
        scope = CoroutineScope(Dispatchers.Unconfined),
        model = model,
        visualisation = { SpotlightSlider(it, model.currentState) },
        gestureFactory = { SpotlightSlider.Gestures(it) },
        animationSpec = animationSpec
    ).also { setupSpotlight(it) }
}

fun <NavTarget : Any> ComposeContentTestRule.setupSpotlight(
    spotlight: Spotlight<NavTarget>,
) {
    setContent {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = appyx_dark
        ) {
            AppyxComponentSetup(spotlight)

            SpotlightUi(
                spotlight = spotlight,
                color = Color.Blue
            )
        }
    }
}

@Composable
fun <NavTarget : Any> SpotlightUi(
    spotlight: Spotlight<NavTarget>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    SampleAppyxContainer(
        appyxComponent = spotlight,
        modifier = modifier
            .padding(
                horizontal = 64.dp,
                vertical = 12.dp
            ),
        elementUi = { element ->
            Element(
                color = color,
                element = element,
                contentDescription =
                "${SPOTLIGHT_EXPERIMENT_TEST_HELPER}_${element.id}"
            )
        }
    )
}

const val SPOTLIGHT_EXPERIMENT_TEST_HELPER = "TheChild"
