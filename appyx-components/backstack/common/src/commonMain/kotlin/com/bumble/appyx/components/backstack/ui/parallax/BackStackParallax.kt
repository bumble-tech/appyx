package com.bumble.appyx.components.backstack.ui.parallax

import DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.components.backstack.BackStackModel.State
import com.bumble.appyx.components.backstack.operation.Pop
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Drag
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragHorizontalDirection
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class BackstackParallax<InteractionTarget : Any>(

    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec,
) {
    private val width = uiContext.transitionBounds.widthDp.value

    private val left = TargetUiState(
        elementWidth = width,
        offsetMultiplier = -1f,
    )
    private val right = TargetUiState(
        elementWidth = width,
        offsetMultiplier = 1f,
    )

    private val bottom = TargetUiState(
        elementWidth = width,
        offsetMultiplier = -0.2f,
    )

    private val top = TargetUiState(
        elementWidth = width,
        offsetMultiplier = 0f,
    )

    override fun State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> {
        val stashed = stashed.mapIndexed { index, element ->
            MatchedTargetUiState(
                element = element,
                targetUiState = if (index == stashed.size - 1) bottom else left,
            )
        }

        return stashed + listOf(
            MatchedTargetUiState(
                element = active,
                targetUiState = top
            )
        ) + (created + destroyed).map {
            MatchedTargetUiState(
                element = it,
                targetUiState = right
            )
        }
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext)

    class Gestures<InteractionTarget : Any>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, State<InteractionTarget>> {
        private val width = transitionBounds.screenWidthDp

        override fun createGesture(
            state: State<InteractionTarget>,
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, State<InteractionTarget>> {
            val widthRight = with(density) { width.toPx() }

            return if  (dragHorizontalDirection(delta) == Drag.HorizontalDirection.RIGHT) {
                Gesture(
                    operation = Pop(),
                    completeAt = Offset(x = widthRight, y = 0f),
                )
            } else {
                Gesture.Noop()
            }
        }
    }
}