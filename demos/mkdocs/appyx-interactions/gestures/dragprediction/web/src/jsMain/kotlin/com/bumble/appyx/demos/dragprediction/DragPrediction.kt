package com.bumble.appyx.demos.dragprediction

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bumble.appyx.components.internal.testdrive.TestDrive
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.B
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.C
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.D
import com.bumble.appyx.demos.dragprediction.DragPredictionMotionController.Companion.toTargetUiState
import com.bumble.appyx.demos.dragprediction.InteractionTarget.Child1
import com.bumble.appyx.interactions.core.DraggableAppyxComponent
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig
import com.bumble.appyx.interactions.core.ui.helper.AppyxComponentSetup

enum class InteractionTarget {
    Child1
}

@Composable
fun DragPrediction(
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val model = remember { TestDriveModel(Child1, null) }
    val testDrive = remember {
        TestDrive(
            scope = coroutineScope,
            model = model,
            progressAnimationSpec = spring(visibilityThreshold = 0.001f),
            motionController = { DragPredictionMotionController(it) },
            gestureFactory = { DragPredictionMotionController.Gestures(it) },
            gestureSettleConfig = GestureSettleConfig(0.25f)
        )
    }

    AppyxComponentSetup(testDrive)

    val output = model.output.collectAsState().value
    val currentTarget: androidx.compose.runtime.State<State<InteractionTarget>?> =
        when (output) {
            is Keyframes -> output.currentSegmentTargetStateFlow.collectAsState(null)
            is Update -> remember(output) { mutableStateOf(output.currentTargetState) }
        }
    val index = when (output) {
        is Keyframes -> output.currentIndex
        is Update -> null
    }

    Box(
        modifier = modifier,
    ) {
        Background(
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            currentTarget = currentTarget.value
        )
        Box(
            modifier = Modifier.padding(24.dp, 24.dp)
        ) {
            Target(elementState = B, alpha = 0.15f)
            Target(elementState = C, alpha = 0.15f)
            Target(elementState = D, alpha = 0.15f)
            Target(elementState = currentTarget.value?.elementState, alpha = 0.65f)
            ModelUi(
                screenWidthPx = screenWidthPx,
                screenHeightPx = screenHeightPx,
                testDrive = testDrive,
                model = model
            )
            Controls(
                testDrive = testDrive
            )
        }
    }
}

@Composable
fun <InteractionTarget : Any> Background(
    screenWidthPx: Int,
    screenHeightPx: Int,
    currentTarget: TestDriveModel.State<InteractionTarget>?,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val backgroundColor1 = animateColorAsState(
        when (currentTarget?.elementState) {
            A -> color_neutral1
            B -> color_neutral2
            C -> color_neutral3
            D -> color_neutral4
            null -> color_bright
        },
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
    )

    Box(
        modifier
            .zIndex(0f)
            .background(
                ShaderBrush(
                    LinearGradientShader(
                        from = Offset.Zero,
                        to = Offset(screenWidthPx.toFloat(), screenHeightPx.toFloat()),
                        colors = listOf(color_bright, backgroundColor1.value)
                    )
                )
            )
    )
}

@Composable
fun Target(
    elementState: ElementState?,
    modifier: Modifier = Modifier,
    alpha: Float
) {
    val targetUiState = elementState?.toTargetUiState()
    targetUiState?.let {
        Box(
            modifier = modifier
                .size(60.dp)
                .offset(targetUiState.position.value.x, targetUiState.position.value.y)
                .scale(targetUiState.scale.value)
                .rotate(targetUiState.rotationZ.value)
                .alpha(alpha)
                .background(
                    color = targetUiState.backgroundColor.value,
                    shape = RoundedCornerShape(targetUiState.roundedCorners.value)
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = elementState.name,
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun <InteractionTarget : Any> ModelUi(
    screenWidthPx: Int,
    screenHeightPx: Int,
    testDrive: TestDrive<InteractionTarget>,
    model: TestDriveModel<InteractionTarget>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    DraggableAppyxComponent(
        appyxComponent = testDrive,
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        modifier = modifier.zIndex(2f)
    ) { elementUiModel ->
        Box(
            modifier = Modifier.size(60.dp)
                .then(elementUiModel.modifier)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = model.output.collectAsState().value.currentTargetState.elementState.name,
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun Controls(
    testDrive: TestDrive<InteractionTarget>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(
        modifier = modifier.zIndex(1f),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = CenterHorizontally
    ) {
        Row {
            Spacer(Modifier.width(16.dp))
        }

    }
}
