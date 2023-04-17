package com.bumble.appyx.interactions

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.transitionmodel.testdrive.TestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.operation.next
import com.bumble.appyx.transitionmodel.testdrive.ui.TestDriveMotionController
import com.bumble.appyx.transitionmodel.testdrive.ui.TestDriveMotionController.Companion.toTargetUiState

enum class NavTarget {
    Child1
}

@Composable
fun TestDriveExperiment(
    ignoreChildrenVisibility: Boolean = false,
) {
    val coroutineScope = rememberCoroutineScope()

    val model = remember {
        TestDriveModel(
            element = NavTarget.Child1,
            savedStateMap = null
        )
    }
    val testDrive = remember {
        TestDrive(
            scope = coroutineScope,
            model = model,
            progressAnimationSpec =
            spring(stiffness = Spring.StiffnessLow)
//                tween(200, easing = LinearEasing)
            ,
            animateSettle = true,
            motionController = {
                TestDriveMotionController(
                    it,
                    uiAnimationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        // reminder: visibilityThreshold is ignored
                    ),
                )
            },
            gestureFactory = { TestDriveMotionController.Gestures(it) }
        )
    }

    InteractionModelSetup(testDrive)

    var size by remember { mutableStateOf(IntSize.Zero) }
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .onSizeChanged { size = it },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TestDriveUi(
            testDrive = testDrive,
            model = model,
            screenWidthPx = size.width,
            screenHeightPx = size.height,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            ignoreChildrenVisibility = ignoreChildrenVisibility,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                testDrive.next(
                    mode = KEYFRAME,
                    animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 1)
                )
            }) {
                Text("Keyframe")
            }

            Button(onClick = {
                testDrive.next(
                    mode = IMMEDIATE,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessVeryLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                )
            }) {
                Text("Immediate")
            }
        }
    }
}

@Composable
fun <InteractionTarget : Any> TestDriveUi(
    testDrive: TestDrive<InteractionTarget>,
    model: TestDriveModel<InteractionTarget>,
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
    ignoreChildrenVisibility: Boolean = false,
) {
    Box(
        modifier
            .padding(
                horizontal = 64.dp,
                vertical = 12.dp
            ),
    ) {
        Children(
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            interactionModel = testDrive,
            ignoreVisibility = ignoreChildrenVisibility,
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
                                Logger.log("drag", "end")
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
