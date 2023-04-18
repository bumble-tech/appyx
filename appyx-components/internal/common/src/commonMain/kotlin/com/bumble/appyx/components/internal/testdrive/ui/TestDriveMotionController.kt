package com.bumble.appyx.components.internal.testdrive.ui

import DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.internal.testdrive.TestDriveModel
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.B
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.C
import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.D
import com.bumble.appyx.components.internal.testdrive.operation.MoveTo
import com.bumble.appyx.components.demos.testdrive.ui.TargetUiState
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.testdrive.ui.md_light_blue_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_light_green_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_red_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_yellow_500
import kotlin.math.abs

class TestDriveMotionController<InteractionTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<InteractionTarget, TestDriveModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = uiAnimationSpec,
) {
    override fun TestDriveModel.State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        listOf(
            MatchedTargetUiState(element, elementState.toTargetUiState()).also {
                Logger.log("TestDrive", "Matched $elementState -> UiState: ${it.targetUiState}")
            }
        )

    companion object {
        val offsetA = DpOffset(0.dp, 0.dp)
        val offsetB = DpOffset(200.dp, 0.dp)
        val offsetC = DpOffset(200.dp, 300.dp)
        val offsetD = DpOffset(0.dp, 300.dp)

        fun TestDriveModel.State.ElementState.toTargetUiState(): TargetUiState =
            when (this) {
                A -> a
                B -> b
                C -> c
                D -> d
            }

        private val a = TargetUiState(
            position = Position.Target(offsetA),
            backgroundColor = BackgroundColor.Target(md_red_500)
        )

        private val b = TargetUiState(
            position = Position.Target(offsetB),
            backgroundColor = BackgroundColor.Target(md_light_green_500)
        )

        private val c = TargetUiState(
            position = Position.Target(offsetC),
            backgroundColor = BackgroundColor.Target(md_yellow_500)
        )

        private val d = TargetUiState(
            position = Position.Target(offsetD),
            backgroundColor = BackgroundColor.Target(md_light_blue_500)
        )
    }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableState(uiContext)

    class Gestures<InteractionTarget>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
        private val width = offsetB.x - offsetA.x

        private val height = offsetD.y - offsetA.y
        override fun createGesture(
            delta: androidx.compose.ui.geometry.Offset,
            density: Density
        ): Gesture<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
            val width = with(density) { width.toPx() }
            val height = with(density) { height.toPx() }

            return if (abs(delta.x) > abs(delta.y)) {
                if (delta.x < 0) {
                    Gesture(
                        operation = MoveTo(D),
                        dragToProgress = { offset -> (offset.x / width) * -1 },
                        partial = { offset, progress -> offset.copy(x = progress * width * -1) }
                    )
                } else {
                    Gesture(
                        operation = MoveTo(B),
                        dragToProgress = { offset -> (offset.x / width) },
                        partial = { offset, partial -> offset.copy(x = partial * width) }
                    )
                }
            } else {
                if (delta.y < 0) {
                    Gesture(
                        operation = MoveTo(A),
                        dragToProgress = { offset -> (offset.y / height) * -1 },
                        partial = { offset, partial -> offset.copy(y = partial * height * -1) }
                    )
                } else {
                    Gesture(
                        operation = MoveTo(C),
                        dragToProgress = { offset -> (offset.y / height) },
                        partial = { offset, partial -> offset.copy(y = partial * height) }
                    )
                }
            }
        }
    }
}

