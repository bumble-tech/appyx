package com.bumble.appyx.components.spotlight.ui.stack3d

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RotationX
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.mapState
import com.bumble.appyx.transitionmodel.BaseMotionController

class SpotlightStack3D<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseMotionController<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
) {
    private val width: Dp = uiContext.transitionBounds.widthDp
    private val height: Dp = uiContext.transitionBounds.heightDp

    private val scrollX = GenericFloatProperty(uiContext, GenericFloatProperty.Target(0f))
    override val geometryMappings: List<Pair<(State<InteractionTarget>) -> Float, GenericFloatProperty>> =
        listOf(
            { state: State<InteractionTarget> -> state.activeIndex } to scrollX
        )

    private val created: TargetUiState = TargetUiState(
        rotationX = RotationX.Target(0f),
        position = Position.Target(DpOffset(0.dp, height)),
        scale = Scale.Target(2.5f),
        alpha = Alpha.Target(0f),
        zIndex = ZIndex.Target(0f),
    )

    private val standard: TargetUiState = TargetUiState(
        rotationX = RotationX.Target(0f),
        position = Position.Target(DpOffset.Zero),
        scale = Scale.Target(1f),
        alpha = Alpha.Target(1f),
        zIndex = ZIndex.Target(0f),
    )

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
                    targetUiState = TargetUiState(
                        base = when (it.value) {
                            CREATED -> created
                            STANDARD -> standard
                            DESTROYED -> destroyed
                        },
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
            scrollX = scrollX.renderValueFlow.mapState(uiContext.coroutineScope) { it - targetUiState.positionInList },
            itemWidth = width,
            itemHeight = height,
        )
}
