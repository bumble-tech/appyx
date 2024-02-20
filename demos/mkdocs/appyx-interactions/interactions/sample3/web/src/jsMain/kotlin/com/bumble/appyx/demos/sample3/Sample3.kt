@file:Suppress("MatchingDeclarationName")
package com.bumble.appyx.demos.sample3

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.bumble.appyx.demos.sample3.InteractionTarget.Child1
import com.bumble.appyx.demos.sample3.Sample3Visualisation.Companion.toTargetUiState
import com.bumble.appyx.interactions.composable.AppyxInteractionsContainer
import com.bumble.appyx.interactions.gesture.GestureReferencePoint
import com.bumble.appyx.interactions.model.transition.Keyframes
import com.bumble.appyx.interactions.model.transition.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.model.transition.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.model.transition.Update
import com.bumble.appyx.interactions.ui.helper.AppyxComponentSetup

enum class InteractionTarget {
    Child1
}

@Composable
fun Sample3(
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
            progressAnimationSpec = spring(
                stiffness = Spring.StiffnessVeryLow / 10,
                visibilityThreshold = 0.001f
            ),
            visualisation = { Sample3Visualisation(it) },
            gestureFactory = { Sample3Visualisation.Gestures(it) }
        )
    }

    AppyxComponentSetup(testDrive)

    val output = model.output.collectAsState().value
    val currentTarget: State<TestDriveModel.State<InteractionTarget>?> =
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
            Target(
                boxScope = this,
                currentTarget = currentTarget.value,
                index = index
            )
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
fun <InteractionTarget : Any> Target(
    boxScope: BoxScope,
    currentTarget: TestDriveModel.State<InteractionTarget>?,
    index: Int?,
    modifier: Modifier = Modifier
) {
    val targetUiState = currentTarget?.elementState?.toTargetUiState()
    targetUiState?.let {
        Box(
            modifier = with(boxScope) {
                modifier
                    .size(60.dp)
                    .align(targetUiState.positionAlignment.value)
                    .offset(
                        x = targetUiState.positionOffset.value.offset.x,
                        y = targetUiState.positionOffset.value.offset.y
                    )
                    .alpha(0.35f)
                    .background(
                        color = targetUiState.backgroundColor.value,
                        shape = RoundedCornerShape(targetUiState.roundedCorners.value)
                    )
            }
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = index?.toString() ?: "X",
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
    AppyxInteractionsContainer(
        appyxComponent = testDrive,
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        gestureRelativeTo = GestureReferencePoint.Element,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(60.dp)
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
            Box(
                modifier = Modifier
                    .background(color_primary, shape = RoundedCornerShape(4.dp))
                    .clickable { testDrive.next(mode = KEYFRAME) }
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Text("Keyframe")
            }
            Spacer(Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .background(color_primary, shape = RoundedCornerShape(4.dp))
                    .clickable {
                        testDrive.next(
                            mode = IMMEDIATE, spring(
                                stiffness = Spring.StiffnessVeryLow,
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        )
                    }
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Text("Immediate")
            }
        }

    }
}
