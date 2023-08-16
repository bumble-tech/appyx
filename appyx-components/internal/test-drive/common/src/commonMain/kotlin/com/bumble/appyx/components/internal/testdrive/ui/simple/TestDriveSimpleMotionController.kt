package com.bumble.appyx.components.internal.testdrive.ui.simple

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
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
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.utils.multiplatform.AppyxLogger

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

        fun TestDriveModel.State.ElementState.toTargetUiState(): TargetUiState =
            when (this) {
                A -> topLeftCorner
                B -> topRightCorner
                C -> bottomRightCorner
                D -> bottomLeftCorner
            }

        private val topLeftCorner = TargetUiState(
            position = PositionInside.Target(BiasAlignment.InsideAlignment.TopStart),
            backgroundColor = BackgroundColor.Target(md_red_500)
        )

        private val topRightCorner = TargetUiState(
            position = PositionInside.Target(BiasAlignment.InsideAlignment.TopEnd),
            backgroundColor = BackgroundColor.Target(md_light_green_500)
        )

        private val bottomRightCorner = TargetUiState(
            position = PositionInside.Target(BiasAlignment.InsideAlignment.CenterEnd),
            backgroundColor = BackgroundColor.Target(md_yellow_500)
        )

        private val bottomLeftCorner = TargetUiState(
            position = PositionInside.Target(BiasAlignment.InsideAlignment.CenterStart),
            backgroundColor = BackgroundColor.Target(md_light_blue_500)
        )
    }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableState(uiContext)

    class Gestures<InteractionTarget>(
        private val transitionBounds: TransitionBounds
    ) : GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> {

        override fun createGesture(
            state: TestDriveModel.State<InteractionTarget>,
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
            val maxX = transitionBounds.widthPx.toFloat()
            val maxY = transitionBounds.heightPx.toFloat() / 2 // Alignment at CenterStart/End

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

