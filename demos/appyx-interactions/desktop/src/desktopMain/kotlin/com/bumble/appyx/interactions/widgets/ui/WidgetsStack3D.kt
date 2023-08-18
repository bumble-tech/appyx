package com.bumble.appyx.interactions.widgets.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.property.impl.RotationX
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.mapState
import com.bumble.appyx.transitionmodel.BaseMotionController

class WidgetsStack3D<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseMotionController<InteractionTarget, SpotlightModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
) {
    private val width: Dp = uiContext.transitionBounds.widthDp
    private val height: Dp = uiContext.transitionBounds.heightDp

    private val scrollY = GenericFloatProperty(uiContext.coroutineScope, GenericFloatProperty.Target(0f))
    override val viewpointDimensions: List<Pair<(SpotlightModel.State<InteractionTarget>) -> Float, GenericFloatProperty>> =
        listOf(
            { state: SpotlightModel.State<InteractionTarget> -> state.activeIndex } to scrollY
        )

    override fun SpotlightModel.State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        positions.flatMapIndexed { index, position ->
            position.elements.map {
                MatchedTargetUiState(
                    element = it.key,
                    targetUiState = TargetUiState(
                        base = default,
                        positionInList = index,
                    )
                )
            }
        }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(
            uiContext = uiContext,
            scrollX = scrollY.renderValueFlow.mapState(uiContext.coroutineScope) { it - targetUiState.positionInList },
            itemWidth = width,
            itemHeight = height,
        )

    private companion object {
        val default: TargetUiState = TargetUiState(
            rotationX = RotationX.Target(0f),
            position = PositionOutside.Target(DpOffset.Zero),
            scale = Scale.Target(1f),
            alpha = Alpha.Target(1f),
            zIndex = ZIndex.Target(0f),
        )
    }
}
