package com.bumble.appyx.interactions.sample

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.first
import com.bumble.appyx.components.spotlight.operation.last
import com.bumble.appyx.components.spotlight.operation.next
import com.bumble.appyx.components.spotlight.operation.previous
import com.bumble.appyx.components.spotlight.operation.updateElements
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.AppyxLogger
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.sample.android.Children
import com.bumble.appyx.interactions.sample.android.Element
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.interactions.sample.InteractionTarget as Target

@ExperimentalMaterialApi
@Composable
@Suppress("LongMethod", "MagicNumber")
fun SpotlightExperiment(
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Horizontal,
    reverseOrientation: Boolean = false,
    motionController: (UiContext) -> BaseMotionController<Target, SpotlightModel.State<Target>, *, *>
) {
    val items = listOf(
        Target.Child1,
        Target.Child2,
        Target.Child3,
        Target.Child4,
        Target.Child5,
        Target.Child6,
        Target.Child7,
        Target.Child1,
        Target.Child2,
        Target.Child3,
        Target.Child4,
        Target.Child5,
        Target.Child6,
        Target.Child7,
        Target.Child1,
        Target.Child2,
        Target.Child3,
        Target.Child4,
        Target.Child5,
        Target.Child6,
        Target.Child7,
    )
    val spotlight = Spotlight(
        model = SpotlightModel(
            items = items,
            savedStateMap = null
        ),
        motionController = motionController,
        gestureFactory = { SpotlightSlider.Gestures(it, orientation, reverseOrientation) },
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 4),
        gestureSettleConfig = GestureSettleConfig(
            completionThreshold = 0.2f,
            completeGestureSpec = spring(),
            revertGestureSpec = spring(),
        ),
    )

    InteractionModelSetup(spotlight)

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
    Children(
        clipToBounds = false,
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
                                AppyxLogger.d("drag", "end")
                                spotlight.onDragEnd()
                            }
                        )
                    }
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SpotlightExperimentInVertical(
    motionController: (UiContext) -> BaseMotionController<Target, SpotlightModel.State<Target>, *, *>
) {
    SpotlightExperiment(
        orientation = Orientation.Vertical,
        reverseOrientation = true,
        motionController = motionController
    )
}

const val SPOTLIGHT_EXPERIMENT_TEST_HELPER = "TheChild"

