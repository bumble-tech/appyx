package com.bumble.appyx.demos.dragprediction

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.B
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.C
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.D
import com.bumble.appyx.components.internal.testdrive.operation.MoveTo
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.DOWN
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.DOWNRIGHT
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.LEFT
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.RIGHT
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.UP
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.UPLEFT
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragDirection8
import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.utils.multiplatform.AppyxLogger

class DragPredictionMotionController<InteractionTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<InteractionTarget, TestDriveModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = uiAnimationSpec,
) {
    override fun TestDriveModel.State<InteractionTarget>.toUiTargets():
            List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        listOf(
            MatchedTargetUiState(element, elementState.toTargetUiState()).also {
                AppyxLogger.d("TestDrive", "Matched $elementState -> UiState: ${it.targetUiState}")
            }
        )

    companion object {
        val offsetA = DpOffset(0.dp, 0.dp)
        val offsetB = DpOffset(324.dp, 0.dp)
        val offsetC = DpOffset(324.dp, 180.dp)
        val offsetD = DpOffset(0.dp, 180.dp)

        fun TestDriveModel.State.ElementState.toTargetUiState(): TargetUiState =
            when (this) {
                A -> uiStateA
                B -> uiStateB
                C -> uiStateC
                D -> uiStateD
            }

        // Top-left corner, A
        private val uiStateA = TargetUiState(
            position = PositionOutside.Target(DpOffset(0.dp, 0.dp)),
            scale = Scale.Target(1f),
            backgroundColor = BackgroundColor.Target(color_primary)
        )

        // Top-right corner, B
        private val uiStateB = TargetUiState(
            position = PositionOutside.Target(DpOffset(180.dp, 30.dp)),
            scale = Scale.Target(2f, TransformOrigin(0f, 0f)),
            backgroundColor = BackgroundColor.Target(color_dark)
        )

        // Bottom-right corner, C
        private val uiStateC = TargetUiState(
            position = PositionOutside.Target(DpOffset(180.dp, 180.dp)),
            scale = Scale.Target(2f, TransformOrigin(0f, 0f)),
            rotationZ = RotationZ.Target(90f),
            backgroundColor = BackgroundColor.Target(color_secondary)
        )

        // Bottom-left corner, D
        private val uiStateD = TargetUiState(
            position = PositionOutside.Target(DpOffset(30.dp, 180.dp)),
            scale = Scale.Target(2f, TransformOrigin(0f, 0f)),
            rotationZ = RotationZ.Target(180f),
            backgroundColor = BackgroundColor.Target(color_tertiary)
        )
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext)


    @Suppress("UnusedPrivateMember")
    class Gestures<InteractionTarget>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
        private val maxX = uiStateB.position.value.offset.x - uiStateA.position.value.offset.x
        private val maxY = uiStateD.position.value.offset.y - uiStateA.position.value.offset.y

        @Suppress("ComplexMethod")
        override fun createGesture(
            state: TestDriveModel.State<InteractionTarget>,
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
            val maxX = with(density) { maxX.toPx() }
            val maxY = with(density) { maxY.toPx() }

            val direction = dragDirection8(delta)
            return when (state.elementState) {
                A -> when (direction) {
                    RIGHT -> Gesture(MoveTo(B), Offset(maxX, 0f))
                    DOWNRIGHT -> Gesture(MoveTo(C), Offset(maxX, maxY))
                    DOWN -> Gesture(MoveTo(D), Offset(0f, maxY))
                    else -> Gesture.Noop()
                }

                B -> when (direction) {
                    LEFT -> Gesture(MoveTo(A), Offset(-maxX, 0f))
                    else -> Gesture.Noop()
                }

                C -> when (direction) {
                    UPLEFT -> Gesture(MoveTo(A), Offset(-maxX, -maxY))
                    else -> Gesture.Noop()
                }

                D -> when (direction) {
                    UP -> Gesture(MoveTo(A), Offset(0f, -maxY))
                    else -> Gesture.Noop()
                }
            }
        }
    }
}

