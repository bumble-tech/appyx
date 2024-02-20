package com.bumble.appyx.components.backstack.ui.parallax

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.components.backstack.BackStackModel.State
import com.bumble.appyx.components.backstack.operation.Pop
import com.bumble.appyx.interactions.ui.context.TransitionBounds
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.gesture.Drag
import com.bumble.appyx.interactions.gesture.Gesture
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.interactions.gesture.dragHorizontalDirection
import com.bumble.appyx.interactions.ui.DefaultAnimationSpec
import com.bumble.appyx.interactions.ui.property.impl.Alpha
import com.bumble.appyx.interactions.ui.property.impl.ColorOverlay
import com.bumble.appyx.interactions.ui.property.impl.Shadow
import com.bumble.appyx.interactions.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation

@Suppress("MagicNumber")
class BackStackParallax<NavTarget : Any>(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseVisualisation<NavTarget, State<NavTarget>, TargetUiState, MutableUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec,
) {
    private val slowOutFastInEasing = CubicBezierEasing(1f, 0f, 1f, 0f)

    private val left = TargetUiState(
        offsetMultiplier = -1f,
        alpha = Alpha.Target(0f),
    )
    private val right = TargetUiState(
        offsetMultiplier = 1f,
        shadow = Shadow.Target(value = 0f, easing = slowOutFastInEasing),
        alpha = Alpha.Target(1f),
    )

    private val bottom = TargetUiState(
        offsetMultiplier = -0.2f,
        colorOverlay = ColorOverlay.Target(0.7f),
        alpha = Alpha.Target(value = 0f, easing = { fraction -> if (fraction == 1f) 1f else 0f })
    )

    private val top = TargetUiState(
        offsetMultiplier = 0f,
        shadow = Shadow.Target(25f),
        alpha = Alpha.Target(value = 1f, easing = { fraction -> if (fraction == 0f) 0f else 1f })
    )

    override fun State<NavTarget>.toUiTargets(): List<MatchedTargetUiState<NavTarget, TargetUiState>> {
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
        targetUiState.toMutableUiState(uiContext)

    class Gestures<NavTarget : Any>(
        private val transitionBounds: TransitionBounds,
    ) : GestureFactory<NavTarget, State<NavTarget>> {

        override val isContinuous: Boolean = false

        override fun createGesture(
            state: State<NavTarget>,
            delta: Offset,
            density: Density
        ): Gesture<NavTarget, State<NavTarget>> {

            return if (dragHorizontalDirection(delta) == Drag.HorizontalDirection.RIGHT) {
                Gesture(
                    operation = Pop(),
                    completeAt = Offset(x = transitionBounds.widthPx.toFloat(), y = 0f),
                )
            } else {
                Gesture.Noop()
            }
        }
    }
}
