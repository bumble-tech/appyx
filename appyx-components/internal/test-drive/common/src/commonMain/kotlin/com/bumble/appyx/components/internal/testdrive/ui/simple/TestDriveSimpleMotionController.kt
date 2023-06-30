package com.bumble.appyx.components.internal.testdrive.ui.simple

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
import com.bumble.appyx.components.internal.testdrive.ui.md_light_blue_500
import com.bumble.appyx.components.internal.testdrive.ui.md_light_green_500
import com.bumble.appyx.components.internal.testdrive.ui.md_red_500
import com.bumble.appyx.components.internal.testdrive.ui.md_yellow_500
import com.bumble.appyx.interactions.AppyxLogger
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
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class TestDriveSimpleMotionController<InteractionTarget : Any>(
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
        val offsetA = DpOffset(0.dp, 0.dp)
        val offsetB = DpOffset(200.dp, 0.dp)
        val offsetC = DpOffset(200.dp, 300.dp)
        val offsetD = DpOffset(0.dp, 300.dp)

        fun TestDriveModel.State.ElementState.toTargetUiState(): TargetUiState =
            when (this) {
                A -> uiStateA
                B -> uiStateB
                C -> uiStateC
                D -> uiStateD
            }

        // Top-left corner, red
        private val uiStateA = TargetUiState(
            position = Position.Target(offsetA),
            backgroundColor = BackgroundColor.Target(md_red_500)
        )

        // Top-right corner, green
        private val uiStateB = TargetUiState(
            position = Position.Target(offsetB),
            backgroundColor = BackgroundColor.Target(md_light_green_500)
        )

        // Bottom-right corner, yellow
        private val uiStateC = TargetUiState(
            position = Position.Target(offsetC),
            backgroundColor = BackgroundColor.Target(md_yellow_500)
        )

        // Bottom-left corner, blue
        private val uiStateD = TargetUiState(
            position = Position.Target(offsetD),
            backgroundColor = BackgroundColor.Target(md_light_blue_500)
        )
    }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableState(uiContext)

    class Gestures<InteractionTarget> : GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
        private val widthDp = offsetB.x - offsetA.x
        private val heightDp = offsetD.y - offsetA.y

        override fun createGesture(
            state: TestDriveModel.State<InteractionTarget>,
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
            val width = with(density) { widthDp.toPx() }
            val height = with(density) { heightDp.toPx() }

            val direction = dragDirection8(delta)
            return when (state.elementState) {
                A -> when (direction) {
                    RIGHT -> Gesture(MoveTo(B), Offset(width, 0f))
                    DOWNRIGHT -> Gesture(MoveTo(C), Offset(width, height))
                    DOWN -> Gesture(MoveTo(D), Offset(0f, height))
                    else -> Gesture.Noop()
                }

                B -> when (direction) {
                    DOWN -> Gesture(MoveTo(C), Offset(0f, height))
                    DOWNLEFT -> Gesture(MoveTo(D), Offset(-width, height))
                    LEFT -> Gesture(MoveTo(A), Offset(-width, 0f))
                    else -> Gesture.Noop()
                }

                C -> when (direction) {
                    LEFT -> Gesture(MoveTo(D), Offset(-width, 0f))
                    UPLEFT -> Gesture(MoveTo(A), Offset(-width, -height))
                    UP -> Gesture(MoveTo(B), Offset(0f, -height))
                    else -> Gesture.Noop()
                }

                D -> when (direction) {
                    UP -> Gesture(MoveTo(A), Offset(0f, -height))
                    UPRIGHT -> Gesture(MoveTo(B), Offset(width, -height))
                    RIGHT -> Gesture(MoveTo(C), Offset(width, 0f))
                    else -> Gesture.Noop()
                }
            }
        }
    }
}

