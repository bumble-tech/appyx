package com.bumble.appyx.components.backstack.ui.stack3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.bumble.appyx.components.backstack.BackStackModel.State
import com.bumble.appyx.components.backstack.operation.Pop
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Drag
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragVerticalDirection
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.TopCenter
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

@Suppress("MagicNumber")
class BackStack3D<InteractionTarget : Any>(
    uiContext: UiContext,
    private val itemsInStack: Int = 3,
) : BaseMotionController<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
) {
    private val height = uiContext.transitionBounds.heightDp

    private val topMost: TargetUiState =
        TargetUiState(
            position = PositionInside.Target(TopCenter, DpOffset(0f.dp, (itemsInStack * 16).dp)),
            scale = Scale.Target(1f, origin = TransformOrigin(0.5f, 0.0f)),
            alpha = Alpha.Target(1f),
            zIndex = ZIndex.Target(itemsInStack.toFloat()),
        )

    private val incoming: TargetUiState =
        TargetUiState(
            position = PositionInside.Target(TopCenter, DpOffset(0f.dp, height)),
            scale = Scale.Target(1f, origin = TransformOrigin(0.5f, 0.0f)),
            alpha = Alpha.Target(0f),
            zIndex = ZIndex.Target(itemsInStack + 1f),
        )

    private fun stacked(stackIndex: Int): TargetUiState =
        TargetUiState(
            position = PositionInside.Target(TopCenter, DpOffset(0f.dp, (itemsInStack - stackIndex) * 16.dp)),
            scale = Scale.Target(1f - stackIndex * 0.05f, origin = TransformOrigin(0.5f, 0.0f)),
            alpha = Alpha.Target(if (stackIndex < itemsInStack) 1f else 0f),
            zIndex = ZIndex.Target(-stackIndex.toFloat()),
        )

    override fun State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        created.mapIndexed { _, element -> MatchedTargetUiState(element, incoming) } +
                listOf(active).map { MatchedTargetUiState(it, topMost) } +
                stashed.mapIndexed { index, element -> MatchedTargetUiState(element, stacked(stashed.size - index)) } +
                destroyed.mapIndexed { _, element -> MatchedTargetUiState(element, incoming) }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableState(uiContext)

    class Gestures<InteractionTarget : Any>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, State<InteractionTarget>> {
        private val height = transitionBounds.screenHeightDp
        override fun createGesture(
            state: State<InteractionTarget>,
            delta: Offset,
            density: Density,
        ): Gesture<InteractionTarget, State<InteractionTarget>> {
            val heightInPx = with(density) { height.toPx() }

            return if (dragVerticalDirection(delta) == Drag.VerticalDirection.DOWN) {
                Gesture(
                    operation = Pop(),
                    completeAt = Offset(x = 0f, y = heightInPx)
                )
            } else {
                Gesture.Noop()
            }
        }
    }
}
