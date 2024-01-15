package com.bumble.appyx.demos.sample1

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
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.BottomEnd
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.TopEnd
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.TopStart
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOffset
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation
import com.bumble.appyx.utils.multiplatform.AppyxLogger

class Sample1Visualisation<InteractionTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseVisualisation<InteractionTarget, TestDriveModel.State<InteractionTarget>, TargetUiState, MutableUiState>(
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
        val bottomOffset = DpOffset(0.dp, (-50).dp)

        fun TestDriveModel.State.ElementState.toTargetUiState(): TargetUiState =
            when (this) {
                A -> topLeftCorner
                B -> topRightCorner
                C -> bottomRightCorner
                D -> bottomLeftCorner
            }

        private val topLeftCorner = TargetUiState(
            positionAlignment = PositionAlignment.Target(TopStart),
            positionOffset = PositionOffset.Target(DpOffset.Zero),
            backgroundColor = BackgroundColor.Target(color_primary)
        )

        private val topRightCorner = TargetUiState(
            positionAlignment = PositionAlignment.Target(TopEnd),
            positionOffset = PositionOffset.Target(DpOffset.Zero),
            backgroundColor = BackgroundColor.Target(color_dark)
        )

        private val bottomRightCorner = TargetUiState(
            positionAlignment = PositionAlignment.Target(BottomEnd),
            positionOffset = PositionOffset.Target(bottomOffset),
            backgroundColor = BackgroundColor.Target(color_secondary)
        )

        private val bottomLeftCorner = TargetUiState(
            positionAlignment = PositionAlignment.Target(InsideAlignment.BottomStart),
            positionOffset = PositionOffset.Target(bottomOffset),
            backgroundColor = BackgroundColor.Target(color_tertiary)
        )
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableUiState(uiContext)

    class Gestures<InteractionTarget>(
        private val transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> {

        @Suppress("ComplexMethod")
        override fun createGesture(
            state: TestDriveModel.State<InteractionTarget>,
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
            // FIXME quick fix – 60.dp is the assumed element size, connect it to real value
            // TODO properly – automate this whole calculation based on .onPlaced centers of targetUiStates
            val maxX = with(density) {
                (transitionBounds.widthDp - 60.dp).toPx()
            }
            val maxY = with(density) {
                (transitionBounds.heightDp + bottomOffset.y - 60.dp).toPx()
            }

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

