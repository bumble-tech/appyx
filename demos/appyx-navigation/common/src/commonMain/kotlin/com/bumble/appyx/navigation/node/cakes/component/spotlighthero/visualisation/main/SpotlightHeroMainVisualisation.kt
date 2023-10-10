package com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.main

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty.Target
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.Center
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.TopCenter
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOffset
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.property.HeroProgress
import com.bumble.appyx.transitionmodel.BaseVisualisation

class SpotlightHeroMainVisualisation<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseVisualisation<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext
) {
    @Suppress("MaxLineLength")
    private val scrollX = GenericFloatProperty(uiContext.coroutineScope, Target(0f))
    override val viewpointDimensions: List<Pair<(State<InteractionTarget>) -> Float, GenericFloatProperty>> =
        listOf(
            { state: State<InteractionTarget> -> state.activeIndex } to scrollX
        )

    private val standard: TargetUiState = TargetUiState(
        positionAlignment = PositionAlignment.Target(Center),
        heroProgress = HeroProgress.Target(0f)
    )

    private val heroElement: TargetUiState = TargetUiState(
        positionAlignment = PositionAlignment.Target(TopCenter),
        positionOffset = PositionOffset.Target(DpOffset(0.dp, (100).dp)),
        heroProgress = HeroProgress.Target(1f)
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
                                else -> standard
                            }
                        },
                        positionInList = index
                    )
                )
            }
        }
    }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableState(uiContext, scrollX.renderValueFlow)

}

