package com.bumble.appyx.components.internal.testdrive.ui.simple

import DefaultAnimationSpec
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
import com.bumble.appyx.interactions.core.ui.gesture.Drag.CompassDirection.E
import com.bumble.appyx.interactions.core.ui.gesture.Drag.CompassDirection.N
import com.bumble.appyx.interactions.core.ui.gesture.Drag.CompassDirection.NE
import com.bumble.appyx.interactions.core.ui.gesture.Drag.CompassDirection.NW
import com.bumble.appyx.interactions.core.ui.gesture.Drag.CompassDirection.S
import com.bumble.appyx.interactions.core.ui.gesture.Drag.CompassDirection.SE
import com.bumble.appyx.interactions.core.ui.gesture.Drag.CompassDirection.SW
import com.bumble.appyx.interactions.core.ui.gesture.Drag.CompassDirection.W
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragCompassDirection
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.testdrive.ui.md_light_blue_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_light_green_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_red_500
import com.bumble.appyx.transitionmodel.testdrive.ui.md_yellow_500

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
        private val widthDp = offsetB.x - offsetA.x
        private val heightDp = offsetD.y - offsetA.y

        override fun createGesture(
            state: TestDriveModel.State<InteractionTarget>,
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
            val width = with(density) { widthDp.toPx() }
            val height = with(density) { heightDp.toPx() }

            val direction = dragCompassDirection(delta)
            return when (state.elementState) {
                A -> when (direction) {
                    E -> Gesture(MoveTo(B), Offset(width, 0f))
                    SE -> Gesture(MoveTo(C), Offset(width, height))
                    S -> Gesture(MoveTo(D), Offset(0f, height))
                    else -> Gesture.Noop()
                }
                B -> when (direction) {
                    S -> Gesture(MoveTo(C), Offset(0f, height))
                    SW -> Gesture(MoveTo(D), Offset(-width, height))
                    W -> Gesture(MoveTo(A), Offset(-width, 0f))
                    else -> Gesture.Noop()
                }
                C -> when (direction) {
                    W -> Gesture(MoveTo(D), Offset(-width, 0f))
                    NW -> Gesture(MoveTo(A), Offset(-width, -height))
                    N -> Gesture(MoveTo(B), Offset(0f, -height))
                    else -> Gesture.Noop()
                }
                D -> when (direction) {
                    N -> Gesture(MoveTo(A), Offset(0f, -height))
                    NE -> Gesture(MoveTo(B), Offset(width, -height))
                    E -> Gesture(MoveTo(C), Offset(width, 0f))
                    else -> Gesture.Noop()
                }
            }
        }
    }
}

