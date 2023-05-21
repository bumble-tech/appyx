package com.bumble.appyx.demos.sample1

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.internal.testdrive.TestDrive
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.operation.next
import com.bumble.appyx.components.internal.testdrive.ui.TestDriveMotionController
import com.bumble.appyx.components.internal.testdrive.ui.TestDriveMotionController.Companion.toTargetUiState
import com.bumble.appyx.interactions.AppyxLogger
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.sample.Children

enum class InteractionTarget {
    Child1
}

@Composable
fun Sample1(
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val model = remember {
        TestDriveModel(
            element = InteractionTarget.Child1,
            savedStateMap = null
        )
    }
    val testDrive = remember {
        TestDrive(
            scope = coroutineScope,
            model = model,
            progressAnimationSpec =
            spring(stiffness = Spring.StiffnessHigh),
            motionController = {
                TestDriveMotionController(
                    it,
                    uiAnimationSpec = spring(
                        stiffness = Spring.StiffnessHigh,
                        dampingRatio = Spring.DampingRatioHighBouncy,
                    ),
                )
            },
            gestureFactory = { TestDriveMotionController.Gestures(it) }
        )
    }

    InteractionModelSetup(testDrive)

    Column(
        modifier = modifier,
    ) {
        TestDriveUi(
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            colors = colors,
            testDrive = testDrive,
            model = model,
            modifier = Modifier.weight(0.9f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier
                    .background(color_dark)
                    .padding(12.dp),

                onClick = {
                    testDrive.next(mode = IMMEDIATE)
                }
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun <InteractionTarget : Any> TestDriveUi(
    screenWidthPx: Int,
    screenHeightPx: Int,
    testDrive: TestDrive<InteractionTarget>,
    model: TestDriveModel<InteractionTarget>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .padding(
                horizontal = 64.dp,
                vertical = 12.dp
            )
    ) {
        Children(
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            interactionModel = testDrive,
        ) { frameModel ->
            Box(
                modifier = Modifier.size(60.dp)
                    .then(frameModel.modifier)
                    .pointerInput(frameModel.element.id) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                testDrive.onDrag(dragAmount, this)
                            },
                            onDragEnd = {
                                AppyxLogger.d("drag", "end")
                                testDrive.onDragEnd()
                            }
                        )
                    }
            )
        }

        val output = model.output.collectAsState().value
        val targetState: State<TestDriveModel.State<InteractionTarget>?> =
            when (output) {
                is Keyframes -> output.currentSegmentTargetStateFlow
                    .collectAsState(null)

                is Update -> remember(output) { mutableStateOf(output.currentTargetState) }
            }
        // FIXME this should be internalised probably
        val targetUiState = targetState.value?.elementState?.toTargetUiState()
        targetUiState?.let {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(targetUiState.position.value.x, targetUiState.position.value.y)
                    .border(2.dp, targetUiState.backgroundColor.value)
                    .semantics {
                        contentDescription = TEST_DRIVE_EXPERIMENT_TEST_HELPER
                    }
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = when (output) {
                        is Keyframes -> output.currentIndex.toString()
                        is Update -> "X"
                    },
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        }
    }
}

const val TEST_DRIVE_EXPERIMENT_TEST_HELPER = "TheSquare"
