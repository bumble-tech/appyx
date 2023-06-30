package com.bumble.appyx.components.modal.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.components.modal.operation.Dismiss
import com.bumble.appyx.components.modal.operation.FullScreen
import com.bumble.appyx.components.modal.operation.Revert
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.dragVerticalDirection
import com.bumble.appyx.interactions.core.ui.gesture.Drag
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.property.impl.Height
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class ModalMotionController<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseMotionController<InteractionTarget, ModalModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext
) {
    private val height = uiContext.transitionBounds.heightDp

    private val createdState: TargetUiState =
        TargetUiState(
            height = Height.Target(0f),
            position = Position.Target(DpOffset(0.dp, 0.dp)),
            corner = RoundedCorners.Target(8),
        )
    private val modalState: TargetUiState =
        TargetUiState(
            height = Height.Target(0.5f),
            position = Position.Target(DpOffset(0.dp, height * 0.5f)),
            corner = RoundedCorners.Target(8),
        )

    private val fullScreenState: TargetUiState =
        TargetUiState(
            height = Height.Target(1f),
            position = Position.Target(DpOffset(0.dp, 0.dp)),
            corner = RoundedCorners.Target(0),
        )

    private val destroyedState: TargetUiState =
        TargetUiState(
            height = Height.Target(1f),
            position = Position.Target(DpOffset(0.dp, height * 1.5f)),
            corner = RoundedCorners.Target(0),
        )

    override fun ModalModel.State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> {
        return created.map { MatchedTargetUiState(it, createdState) } +
                listOfNotNull(modal).map { MatchedTargetUiState(it, modalState) } +
                listOfNotNull(fullScreen).map { MatchedTargetUiState(it, fullScreenState) } +
                destroyed.map { MatchedTargetUiState(it, destroyedState) }
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState = targetUiState.toMutableState(uiContext)

    class Gestures<InteractionTarget : Any>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, ModalModel.State<InteractionTarget>> {
        private val height = transitionBounds.heightPx.toFloat()

        override fun createGesture(
            state: ModalModel.State<InteractionTarget>,
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, ModalModel.State<InteractionTarget>> =
            when (dragVerticalDirection(delta)) {
                Drag.VerticalDirection.UP -> Gesture(
                    operation = FullScreen(),
                    completeAt = Offset(0f, -height)
                )

                Drag.VerticalDirection.DOWN -> Gesture(
                    operation = if (state.fullScreen == null) Dismiss() else Revert(),
                    completeAt = Offset(0f, height)
                )

                else -> Gesture.Noop()
            }
    }
}
