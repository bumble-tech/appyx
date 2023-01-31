package com.bumble.appyx.interactions.sample

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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.core.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.Keyframes
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.ui.InteractionModelSetup
import com.bumble.appyx.interactions.sample.NavTarget.Child1
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.transitionmodel.testdrive.TestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.interpolator.TestDriveUiModel
import com.bumble.appyx.transitionmodel.testdrive.interpolator.TestDriveUiModel.Companion.toProps
import com.bumble.appyx.transitionmodel.testdrive.operation.next


@ExperimentalMaterialApi
@Composable
fun TestDriveExperiment() {
    val coroutineScope = rememberCoroutineScope()

    val model = remember { TestDriveModel(Child1) }
    val testDrive = remember {
        TestDrive(
            scope = coroutineScope,
            model = model,
            interpolator = { TestDriveUiModel(it, spring(
                stiffness = Spring.StiffnessVeryLow / 2,
                dampingRatio = Spring.DampingRatioLowBouncy,
            )) },
            gestureFactory = { TestDriveUiModel.Gestures(it) }
        )
    }

    InteractionModelSetup(testDrive)

    Column(
        Modifier
            .fillMaxWidth()
            .background(appyx_dark)
    ) {
        Box(
            Modifier
                .weight(0.9f)
                .padding(
                    horizontal = 64.dp,
                    vertical = 12.dp
                )
        ) {
            Children(
                interactionModel = testDrive,
            ) { frameModel ->
                Box(
                    modifier = Modifier.size(60.dp)
                        .then(frameModel.modifier)
                        .pointerInput(frameModel.navElement.id) {
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
            val targetProps = output.currentTargetState.elementState.toProps()
            Box(modifier = Modifier
                .size(60.dp)
                .offset(targetProps.offset.value.x, targetProps.offset.value.y)
                .border(2.dp, targetProps.backgroundColor.value)
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { testDrive.next(
                mode = KEYFRAME,
                animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 5)
            ) }) {
                Text("Keyframe")
            }

            Button(onClick = { testDrive.next(
                mode = IMMEDIATE,
                // FIXME this animationSpec affects only progress, not the update!
                animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 5)
            ) }) {
                Text("Immediate")
            }
        }
    }
}
