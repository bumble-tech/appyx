package com.bumble.appyx.components.spotlight.ui.stack3d

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment.Companion.InContainer
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment.Companion.OutsideBottom
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment.Companion.OutsideTop
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.mapState
import com.bumble.appyx.transitionmodel.BaseMotionController

@Suppress("MagicNumber")
class SpotlightStack3D<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseMotionController<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
) {
    private var width: Dp = 0.dp
    private var height: Dp = 0.dp

    override fun updateBounds(transitionBounds: TransitionBounds) {
        super.updateBounds(transitionBounds)
        width = transitionBounds.widthDp
        height = transitionBounds.heightDp
    }

    private val scrollY = GenericFloatProperty(uiContext.coroutineScope, GenericFloatProperty.Target(0f))
    override val viewpointDimensions: List<Pair<(State<InteractionTarget>) -> Float, GenericFloatProperty>> =
        listOf(
            { state: State<InteractionTarget> -> state.activeIndex } to scrollY
        )

    private val created: TargetUiState = TargetUiState(
        position = PositionOutside.Target(OutsideTop),
        scale = Scale.Target(2.5f),
        alpha = Alpha.Target(0f),
        zIndex = ZIndex.Target(0f),
    )

    private val standard: TargetUiState = TargetUiState(
        position = PositionOutside.Target(InContainer),
        scale = Scale.Target(1f),
        alpha = Alpha.Target(1f),
        zIndex = ZIndex.Target(0f),
    )

    private val destroyed: TargetUiState = TargetUiState(
        position = PositionOutside.Target(OutsideBottom),
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
            scrollX = scrollY.renderValueFlow.mapState(uiContext.coroutineScope) { it - targetUiState.positionInList },
            itemHeight = height,
        )
}
