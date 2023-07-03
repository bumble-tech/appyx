package com.bumble.appyx.demos.incompletedrag

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.B
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.C
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.D
import com.bumble.appyx.components.internal.testdrive.operation.MoveTo
import com.bumble.appyx.interactions.AppyxLogger
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.DOWN
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.DOWNLEFT
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.DOWNRIGHT
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.LEFT
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.RIGHT
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.UP
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.UPLEFT
import com.bumble.appyx.interactions.core.ui.gesture.Drag.Direction8.UPRIGHT
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragDirection8
import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class IncompleteDragMotionController<InteractionTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<InteractionTarget, TestDriveModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = uiAnimationSpec,
) {
    override fun TestDriveModel.State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        listOf(
            MatchedTargetUiState(element, elementState.toTargetUiState()).also {
                AppyxLogger.d("TestDrive", "Matched $elementState -> UiState: ${it.targetUiState}")
            }
        )

    companion object {

        fun TestDriveModel.State.ElementState.toTargetUiState(): TargetUiState =
            when (this) {
                A -> uiStateA
                B -> uiStateB
                C -> uiStateC
                D -> uiStateD
            }

        // Top-left corner, A
        private val uiStateA = TargetUiState(
            position = Position.Target(DpOffset(0.dp, 0.dp)),
            rotationZ = RotationZ.Target(0f),
            backgroundColor = BackgroundColor.Target(color_primary)
        )

        // Top-right corner, B
        private val uiStateB = TargetUiState(
            position = Position.Target(DpOffset(370.dp, 0.dp)),
            rotationZ = RotationZ.Target(180f),
            backgroundColor = BackgroundColor.Target(color_dark)
        )

        // Bottom-right corner, C
        private val uiStateC = TargetUiState(
            position = Position.Target(DpOffset(370.dp, 180.dp)),
            rotationZ = RotationZ.Target(270f),
            backgroundColor = BackgroundColor.Target(color_secondary)
        )

        // Bottom-left corner, D
        private val uiStateD = TargetUiState(
            position = Position.Target(DpOffset(0.dp, 180.dp)),
            rotationZ = RotationZ.Target(540f),
            backgroundColor = BackgroundColor.Target(color_tertiary)
        )
    }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableState(uiContext)


    class Gestures<InteractionTarget>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
        private val maxX = uiStateB.position.value.x - uiStateA.position.value.x
        private val maxY = uiStateD.position.value.y - uiStateA.position.value.y

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
                    DOWN -> Gesture(MoveTo(C), Offset(0f, maxY))
                    DOWNLEFT -> Gesture(MoveTo(D), Offset(-maxX, maxY))
                    LEFT -> Gesture(MoveTo(A), Offset(-maxX, 0f))
                    else -> Gesture.Noop()
                }

                C -> when (direction) {
                    LEFT -> Gesture(MoveTo(D), Offset(-maxX, 0f))
                    UPLEFT -> Gesture(MoveTo(A), Offset(-maxX, -maxY))
                    UP -> Gesture(MoveTo(B), Offset(0f, -maxY))
                    else -> Gesture.Noop()
                }

                D -> when (direction) {
                    UP -> Gesture(MoveTo(A), Offset(0f, -maxY))
                    UPRIGHT -> Gesture(MoveTo(B), Offset(maxX, -maxY))
                    RIGHT -> Gesture(MoveTo(C), Offset(maxX, 0f))
                    else -> Gesture.Noop()
                }
            }
        }
    }
}

