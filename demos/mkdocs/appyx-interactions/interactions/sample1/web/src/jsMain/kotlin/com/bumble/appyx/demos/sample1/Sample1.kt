package com.bumble.appyx.demos.sample1

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bumble.appyx.components.internal.testdrive.TestDrive
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.B
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.C
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.D
import com.bumble.appyx.components.internal.testdrive.operation.next
import com.bumble.appyx.demos.sample1.InteractionTarget.Child1
import com.bumble.appyx.interactions.core.DraggableAppyxComponent
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.core.ui.helper.AppyxComponentSetup

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
    val model = remember { TestDriveModel(Child1, null) }
    val testDrive = remember {
        TestDrive(
            scope = coroutineScope,
            model = model,
            motionController = { Sample1MotionController(it) },
            gestureFactory = { Sample1MotionController.Gestures(it) }
        )
    }

    AppyxComponentSetup(testDrive)

    Box(
        modifier = modifier,
    ) {
        Background(
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            model = model
        )
        Box(
            modifier = Modifier.padding(24.dp, 24.dp)
        ) {
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
    model: TestDriveModel<InteractionTarget>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val output = model.output.collectAsState()
    val currentTarget = output.value.currentTargetState.elementState
    val backgroundColor1 = animateColorAsState(
        when (currentTarget) {
            A -> color_neutral1
            B -> color_neutral2
            C -> color_neutral3
            D -> color_neutral4
        }
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
fun <InteractionTarget : Any> ModelUi(
    screenWidthPx: Int,
    screenHeightPx: Int,
    testDrive: TestDrive<InteractionTarget>,
    model: TestDriveModel<InteractionTarget>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val output = model.output.collectAsState()
    val currentTarget = output.value.currentTargetState.elementState
    val backgroundColor1 = animateColorAsState(
        when (currentTarget) {
            A -> color_neutral1
            B -> color_neutral2
            C -> color_neutral3
            D -> color_neutral4
        }
    )

    DraggableAppyxComponent(
        appyxComponent = testDrive,
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
    ) { elementUiModel ->
        Box(
            modifier = Modifier
                .size(60.dp)
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
        Box(
            modifier = Modifier
                .background(color_primary, shape = RoundedCornerShape(4.dp))
                .clickable {
                    testDrive.next(
                        mode = IMMEDIATE, animationSpec = spring(
                            stiffness = Spring.StiffnessMedium,
                            dampingRatio = Spring.DampingRatioLowBouncy,
                        )
                    )
                }
                .padding(horizontal = 18.dp, vertical = 9.dp)
        ) {
            Text("Next")
        }
    }
}
