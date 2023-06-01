package com.bumble.appyx.components.spotlight.ui.sliderscale

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty.Target
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class SpotlightSliderScale<InteractionTarget : Any>(
    uiContext: UiContext,
    private val orientation: Orientation = Orientation.Horizontal, // TODO support RTL
) : BaseMotionController<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext
) {
    private val width: Dp = uiContext.transitionBounds.widthDp
    private val height: Dp = uiContext.transitionBounds.heightDp
    private val scrollX = GenericFloatProperty(
        uiContext,
        Target(0f)
    ) // TODO sync this with the model's initial value rather than assuming 0
    override val geometryMappings: List<Pair<(State<InteractionTarget>) -> Float, GenericFloatProperty>> =
        listOf(
            { state: State<InteractionTarget> -> state.activeIndex } to scrollX
        )

    private val created: TargetUiState = TargetUiState(
        position = Position.Target(DpOffset(0.dp, width)),
        scale = Scale.Target(value = Scale.Target.Scale(0f, 0f)),
    )

    private val standard: TargetUiState = TargetUiState(
        position = Position.Target(DpOffset.Zero),
        scale = Scale.Target(value = Scale.Target.Scale(1f, 1f)),
    )

    private val destroyed: TargetUiState = TargetUiState(
        position = Position.Target(DpOffset(x = 0.dp, y = -height)),
        scale = Scale.Target(value = Scale.Target.Scale(0f, 0f)),
    )

    override fun State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> {
        return positions.flatMapIndexed { index, position ->
            position.elements.map {
                MatchedTargetUiState(
                    element = it.key,
                    targetUiState = TargetUiState(
                        base = when (it.value) {
                            CREATED -> created
                            STANDARD -> standard
                            DESTROYED -> destroyed
                        },
                        positionInList = index,
                        elementWidth = width
                    )
                )
            }
        }
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext, scrollX.renderValueFlow, width)
}

