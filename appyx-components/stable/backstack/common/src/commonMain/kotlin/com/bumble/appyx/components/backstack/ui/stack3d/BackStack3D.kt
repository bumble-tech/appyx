package com.bumble.appyx.components.backstack.ui.stack3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
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
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOffset
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation

@Suppress("MagicNumber")
class BackStack3D<NavTarget : Any>(
    uiContext: UiContext,
    private val itemsInStack: Int = 3,
) : BaseVisualisation<NavTarget, State<NavTarget>, TargetUiState, MutableUiState>(
    uiContext = uiContext,
) {

    private val topMost: TargetUiState =
        TargetUiState(
            positionAlignment = PositionAlignment.Target(TopCenter),
            positionOffset = PositionOffset.Target(DpOffset(0f.dp, (itemsInStack * 16).dp)),
            scale = Scale.Target(1f, origin = TransformOrigin(0.5f, 0.0f)),
            alpha = Alpha.Target(1f),
            zIndex = ZIndex.Target(itemsInStack.toFloat()),
        )

    private lateinit var incoming: TargetUiState

    override fun updateBounds(transitionBounds: TransitionBounds) {
        super.updateBounds(transitionBounds)
        incoming = incoming(transitionBounds.heightDp)
    }

    private fun stacked(stackIndex: Int): TargetUiState =
        TargetUiState(
            positionAlignment = PositionAlignment.Target(TopCenter),
            positionOffset = PositionOffset.Target(DpOffset(0f.dp, (itemsInStack - stackIndex) * 16.dp)),
            scale = Scale.Target(1f - stackIndex * 0.05f, origin = TransformOrigin(0.5f, 0.0f)),
            alpha = Alpha.Target(if (stackIndex < itemsInStack) 1f else 0f),
            zIndex = ZIndex.Target(-stackIndex.toFloat()),
        )

    private fun incoming(height: Dp): TargetUiState = TargetUiState(
        positionAlignment = PositionAlignment.Target(TopCenter),
        positionOffset = PositionOffset.Target(DpOffset(0f.dp, height)),
        scale = Scale.Target(1f, origin = TransformOrigin(0.5f, 0.0f)),
        alpha = Alpha.Target(0f),
        zIndex = ZIndex.Target(itemsInStack + 1f),
    )

    override fun State<NavTarget>.toUiTargets(): List<MatchedTargetUiState<NavTarget, TargetUiState>> =
        created.mapIndexed { _, element -> MatchedTargetUiState(element, incoming) } +
                listOf(active).map { MatchedTargetUiState(it, topMost) } +
                stashed.mapIndexed { index, element ->
                    MatchedTargetUiState(
                        element,
                        stacked(stashed.size - index)
                    )
                } +
                destroyed.mapIndexed { _, element -> MatchedTargetUiState(element, incoming) }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableUiState(uiContext)

    class Gestures<NavTarget : Any>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<NavTarget, State<NavTarget>> {

        override val isContinuous: Boolean = false

        private val height = transitionBounds.screenHeightDp

        override fun createGesture(
            state: State<NavTarget>,
            delta: Offset,
            density: Density,
        ): Gesture<NavTarget, State<NavTarget>> {
            val heightInPx = with(density) { height.toPx() }

            return if (dragVerticalDirection(delta) == Drag.VerticalDirection.DOWN) {
                Gesture(
                    operation = Pop(),
                    completeAt = Offset(x = 0f, y = heightInPx),
                )
            } else {
                Gesture.Noop()
            }
        }
    }
}
