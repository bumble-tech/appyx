package com.bumble.appyx.components.backstack.ui.stack3d

import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class BackStack3D<InteractionTarget : Any>(
    uiContext: UiContext,
    private val itemsInStack: Int = 3,
) : BaseMotionController<InteractionTarget, BackStackModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
) {
    private val height = uiContext.transitionBounds.heightDp

    private val topMost: TargetUiState =
        TargetUiState(
            position = Position.Target(DpOffset(0f.dp, (itemsInStack * 16).dp)),
            scale = Scale.Target(1f, origin = TransformOrigin(0.5f, 0.0f)),
            alpha = Alpha.Target(1f),
            zIndex = ZIndex.Target(itemsInStack.toFloat()),
        )

    private val incoming: TargetUiState =
        TargetUiState(
            position = Position.Target(DpOffset(0f.dp, height)),
            scale = Scale.Target(1f, origin = TransformOrigin(0.5f, 0.0f)),
            alpha = Alpha.Target(0f),
            zIndex = ZIndex.Target(itemsInStack + 1f),
        )

    private fun stacked(stackIndex: Int): TargetUiState =
        TargetUiState(
            position = Position.Target(DpOffset(0f.dp, (itemsInStack - stackIndex) * 16.dp)),
            scale = Scale.Target(1f - stackIndex * 0.05f, origin = TransformOrigin(0.5f, 0.0f)),
            alpha = Alpha.Target(if (stackIndex < itemsInStack) 1f else 0f),
            zIndex = ZIndex.Target(-stackIndex.toFloat()),
        )

    override fun BackStackModel.State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        created.mapIndexed { _, element -> MatchedTargetUiState(element, incoming) } +
                listOf(active).map { MatchedTargetUiState(it, topMost) } +
                stashed.mapIndexed { index, element -> MatchedTargetUiState(element, stacked(stashed.size - index)) } +
                destroyed.mapIndexed { _, element -> MatchedTargetUiState(element, incoming) }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableState(uiContext)

}
