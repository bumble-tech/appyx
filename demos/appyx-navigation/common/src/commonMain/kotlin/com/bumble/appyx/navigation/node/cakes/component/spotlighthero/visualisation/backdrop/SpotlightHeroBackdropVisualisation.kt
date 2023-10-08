package com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.backdrop

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty.Target
import com.bumble.appyx.interactions.core.ui.property.impl.Height
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.Center
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.TopEnd
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.transitionmodel.BaseVisualisation

class SpotlightHeroBackdropVisualisation<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseVisualisation<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext
) {
    private val scrollX = GenericFloatProperty(
        uiContext.coroutineScope,
        Target(0f)
    )
    override val viewpointDimensions: List<Pair<(State<InteractionTarget>) -> Float, GenericFloatProperty>> =
        listOf(
            { state: State<InteractionTarget> -> state.activeIndex } to scrollX
        )

    private val standard: TargetUiState = TargetUiState(
        positionAlignment = PositionAlignment.Target(
            insideAlignment = Center
        ),
        height = Height.Target(0.5f),
        roundedCorners = RoundedCorners.Target(5),
    )

    private val heroElement: TargetUiState = TargetUiState(
        positionAlignment = PositionAlignment.Target(
            insideAlignment = TopEnd
        ),
        height = Height.Target(0.25f),
        roundedCorners = RoundedCorners.Target(0),
    )

    private val hidden: TargetUiState = TargetUiState(
        alpha = Alpha.Target(0f),
    )

    override fun State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> {
        return positions.flatMapIndexed { index, position ->
            position.elements.map {
                MatchedTargetUiState(
                    element = it.key,
                    targetUiState = TargetUiState(
                        base = when (mode) {
                            LIST -> standard
                            HERO -> when (index.toFloat()) {
                                activeIndex -> heroElement
                                else -> hidden
                            }
                        },
                        positionInList = index
                    )
                )
            }
        }
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext, scrollX.renderValueFlow)
}

