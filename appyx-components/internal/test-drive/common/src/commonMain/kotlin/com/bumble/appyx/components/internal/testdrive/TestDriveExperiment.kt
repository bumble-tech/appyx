package com.bumble.appyx.components.internal.testdrive

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.internal.testdrive.operation.next
import com.bumble.appyx.components.internal.testdrive.ui.rotation.TestDriveRotationVisualisation
import com.bumble.appyx.components.internal.testdrive.ui.rotation.TestDriveRotationVisualisation.Companion.toTargetUiState
import com.bumble.appyx.components.internal.testdrive.ui.simple.TestDriveSimpleVisualisation
import com.bumble.appyx.interactions.core.AppyxInteractionsContainer
import com.bumble.appyx.interactions.gesture.GestureValidator
import com.bumble.appyx.interactions.gesture.GestureValidator.Companion.defaultValidator
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.interactions.core.ui.helper.AppyxComponentSetup

@Suppress("MagicNumber", "LongMethod")
@Composable
fun <InteractionTarget : Any> TestDriveExperiment(
    screenWidthPx: Int,
    screenHeightPx: Int,
    element: InteractionTarget,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val model = remember {
        TestDriveModel(
            element = element,
            savedStateMap = null
        )
    }
    val testDrive = remember {
        TestDrive(
            scope = coroutineScope,
            model = model,
            progressAnimationSpec = spring(stiffness = Spring.StiffnessLow),
            animateSettle = true,
            visualisation = {
                TestDriveRotationVisualisation(
                    it,
                    uiAnimationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        // reminder: visibilityThreshold is ignored
                    ),
                )
            },
            gestureFactory = { TestDriveSimpleVisualisation.Gestures(it) }
        )
    }

    AppyxComponentSetup(testDrive)

    Column(
        modifier = modifier,
    ) {
        TestDriveUi(
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
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
    screenWidthPx: Int,
    screenHeightPx: Int,
    testDrive: TestDrive<InteractionTarget>,
    model: TestDriveModel<InteractionTarget>,
    modifier: Modifier = Modifier,
    gestureValidator: GestureValidator = defaultValidator,
) {
    Box(
        modifier
            .padding(
                horizontal = 64.dp,
                vertical = 12.dp
            )
    ) {
        AppyxInteractionsContainer(
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            appyxComponent = testDrive,
            gestureValidator = gestureValidator,
        ) { elementUiModel ->
            Box(
                modifier = Modifier.size(60.dp)
                    .then(elementUiModel.modifier)
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
                    .align(targetUiState.positionAlignment.value)
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
