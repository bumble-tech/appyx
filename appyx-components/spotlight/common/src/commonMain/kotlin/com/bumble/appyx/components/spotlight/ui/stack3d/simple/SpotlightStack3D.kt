package com.bumble.appyx.components.spotlight.ui.stack3d.simple

import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RotationX
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import kotlin.math.roundToInt

class SpotlightStack3D<InteractionTarget : Any>(
    uiContext: UiContext,
    private val itemsInStack: Int = DEFAULT_ITEMS_IN_STACK,
) : BaseMotionController<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
) {
    private val width: Dp = uiContext.transitionBounds.widthDp
    private val height: Dp = uiContext.transitionBounds.heightDp

    private val created: TargetUiState = TargetUiState(
        rotationX = RotationX.Target(0f),
        position = Position.Target(DpOffset(0.dp, height)),
        scale = Scale.Target(2.5f),
        alpha = Alpha.Target(0f),
        zIndex = ZIndex.Target(0f),
    )

    private val topMost: TargetUiState = TargetUiState(
        rotationX = RotationX.Target(0f),
        position = Position.Target(DpOffset.Zero),
        scale = Scale.Target(1f),
        alpha = Alpha.Target(1f),
        zIndex = ZIndex.Target(0f),
    )

    private fun stacked(effectiveIndex: Int): TargetUiState = TargetUiState(
        rotationX = RotationX.Target(value = -2.5f, origin = TransformOrigin(0.075f, 0f)),
        position = Position.Target(
            DpOffset(
                (-0.125f * effectiveIndex * width.value).dp,
                (-0.075f * effectiveIndex * height.value / (1f + 0.1 * effectiveIndex)).dp
            )
        ),
        scale = Scale.Target(1f - 0.1f * effectiveIndex),
        alpha = Alpha.Target(if (effectiveIndex <= itemsInStack) 1f else 0f),
        zIndex = ZIndex.Target(-effectiveIndex.toFloat()),
    )

    private val discarded: TargetUiState = TargetUiState(
        rotationX = RotationX.Target(0f),
        position = Position.Target(DpOffset(0.dp, height)),
        scale = Scale.Target(1f),
        alpha = Alpha.Target(0f),
        zIndex = ZIndex.Target(1f),
    )

    private fun standard(index: Int, activeIndex: Int): TargetUiState {
        val effectivePosition = index - activeIndex
        return when {
            effectivePosition == 0 -> topMost
            effectivePosition > 0 -> stacked(effectivePosition)
            else -> discarded
        }
    }

    private val destroyed: TargetUiState = TargetUiState(
        rotationX = RotationX.Target(0f),
        position = Position.Target(DpOffset(0.dp, -height)),
        scale = Scale.Target(0.25f),
        alpha = Alpha.Target(0f),
        zIndex = ZIndex.Target(0f),
    )

    override fun State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        positions.flatMapIndexed { index, position ->
            position.elements.map {
                MatchedTargetUiState(
                    element = it.key,
                    targetUiState =
                    when (it.value) {
                        CREATED -> created
                        STANDARD -> standard(index, activeIndex.roundToInt())
                        DESTROYED -> destroyed
                    },
                )
            }
        }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext = uiContext)

    companion object {
        const val DEFAULT_ITEMS_IN_STACK = 3
    }
}
