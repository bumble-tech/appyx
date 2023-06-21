package com.bumble.appyx.components.modal.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.components.modal.operation.FullScreen
import com.bumble.appyx.components.modal.operation.Revert
import com.bumble.appyx.components.modal.operation.Show
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.*
import com.bumble.appyx.interactions.core.ui.property.impl.*
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class ModalMotionController<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseMotionController<InteractionTarget, ModalModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext
) {
    private val width = uiContext.transitionBounds.widthDp
    private val height = uiContext.transitionBounds.heightDp

    private val createdState: TargetUiState =
        TargetUiState(
            width = Width.Target(0f),
            height = Height.Target(0f),
            position = Position.Target(DpOffset(0.dp, 0.dp)),
            corner = BackgroundCorner.Target(40f),
        )
    private val modalState: TargetUiState =
        TargetUiState(
            width = Width.Target(0.7f),
            height = Height.Target(0.5f),
            position = Position.Target(DpOffset(0.dp, height * 0.5f)),
            corner = BackgroundCorner.Target(40f),
        )

    private val fullScreenState: TargetUiState =
        TargetUiState(
            width = Width.Target(1f),
            height = Height.Target(1f),
            position = Position.Target(DpOffset(0.dp, 0.dp)),
            corner = BackgroundCorner.Target(0.1f),
        )
    
    private val destroyedState: TargetUiState =
        TargetUiState(
            width = Width.Target(1f),
            height = Height.Target(1f),
            position = Position.Target(DpOffset(width, 0.dp)),
            corner = BackgroundCorner.Target(0.1f),
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

    class ModalGestures<InteractionTarget : Any>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, ModalModel.State<InteractionTarget>> {
        private val height = transitionBounds.heightPx.toFloat()
        private val width = transitionBounds.widthPx.toFloat()

        override fun createGesture(
            state: ModalModel.State<InteractionTarget>,
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, ModalModel.State<InteractionTarget>> =
            when (dragDirection4(delta)) {
                Drag.Direction4.UP -> Gesture(
                    operation = FullScreen(),
                    completeAt = Offset(0f, -height)
                )
                Drag.Direction4.DOWN -> Gesture(
                    operation = Revert(),
                    completeAt = Offset(0f, height)
                )
                Drag.Direction4.RIGHT -> Gesture(
                    operation = Show(),
                    completeAt = Offset(width, 0f)
                )
                else -> {
                    Gesture(
                        operation = Revert(),
                        completeAt = Offset(0f, height)
                    )
                }
            }
    }
}
