package com.bumble.appyx.interactions.sample

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.first
import com.bumble.appyx.components.spotlight.operation.last
import com.bumble.appyx.components.spotlight.operation.next
import com.bumble.appyx.components.spotlight.operation.previous
import com.bumble.appyx.components.spotlight.operation.updateElements
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.gesture.GestureSettleConfig
import com.bumble.appyx.interactions.ui.helper.AppyxComponentSetup
import com.bumble.appyx.interactions.utils.testing.TestTarget
import com.bumble.appyx.interactions.utils.ui.Element
import com.bumble.appyx.interactions.utils.ui.SampleAppyxContainer
import com.bumble.appyx.interactions.utils.ui.theme.appyx_dark

@Composable
@Suppress("LongMethod", "MagicNumber")
fun SpotlightExperiment(
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Horizontal,
    reverseOrientation: Boolean = false,
    visualisationType: SpotlightVisualisationType = SpotlightVisualisationType.SLIDER_ROTATION,
) {
    val items = listOf(
        TestTarget.Child1,
        TestTarget.Child2,
        TestTarget.Child3,
        TestTarget.Child4,
        TestTarget.Child5,
        TestTarget.Child6,
        TestTarget.Child7,
        TestTarget.Child1,
        TestTarget.Child2,
        TestTarget.Child3,
        TestTarget.Child4,
        TestTarget.Child5,
        TestTarget.Child6,
        TestTarget.Child7,
        TestTarget.Child1,
        TestTarget.Child2,
        TestTarget.Child3,
        TestTarget.Child4,
        TestTarget.Child5,
        TestTarget.Child6,
        TestTarget.Child7,
    )
    val model = remember {
        SpotlightModel(
            items = items,
            savedStateMap = null,
        )
    }
    val spotlight = remember {
        Spotlight(
            model = model,
            visualisation = { visualisationType.toVisualisation(it, model.currentState) },
            gestureFactory = { SpotlightSlider.Gestures(it, orientation, reverseOrientation) },
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 4),
            gestureSettleConfig = GestureSettleConfig(
                completionThreshold = 0.2f,
                completeGestureSpec = spring(),
                revertGestureSpec = spring(),
            ),
        )
    }

    AppyxComponentSetup(spotlight)

    Column(
        modifier
            .fillMaxWidth()
            .background(appyx_dark)
    ) {
        SpotlightUi(
            spotlight = spotlight,
            modifier = Modifier.weight(0.9f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                spotlight.updateElements(
                    items,
                    animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 20)
                )
            }) {
                Text("New")
            }
            Button(onClick = { spotlight.first() }) {
                Text("First")
            }
            Button(onClick = { spotlight.previous() }) {
                Text("Prev")
            }
            Button(onClick = { spotlight.next() }) {
                Text("Next")
            }
            Button(onClick = { spotlight.last() }) {
                Text("Last")
            }
        }
    }
}

@Composable
fun <InteractionTarget : Any> SpotlightUi(
    spotlight: Spotlight<InteractionTarget>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    SampleAppyxContainer(
        clipToBounds = false,
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

@Composable
fun SpotlightExperimentInVertical() {
    SpotlightExperiment(
        orientation = Orientation.Vertical,
        reverseOrientation = true,
    )
}

const val SPOTLIGHT_EXPERIMENT_TEST_HELPER = "TheChild"

