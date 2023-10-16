package com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.backdrop

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.AspectRatio
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Height
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.Center
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.TopEnd
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOffset
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroVisualisation
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.property.HeroProgress
import com.bumble.appyx.transitionmodel.BaseVisualisation

class SpotlightHeroBackdropVisualisation<InteractionTarget : Any>(
    uiContext: UiContext
) : SpotlightHeroVisualisation<InteractionTarget>,
    BaseVisualisation<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext
) {
    private val scrollX = GenericFloatProperty(uiContext.coroutineScope, GenericFloatProperty.Target(0f))
    override val heroProgress = HeroProgress(uiContext.coroutineScope, GenericFloatProperty.Target(0f))
    override val viewpointDimensions: List<Pair<(State<InteractionTarget>) -> Float, GenericFloatProperty>> =
        listOf(
            { state: State<InteractionTarget> -> state.activeIndex } to scrollX,
            { state: State<InteractionTarget> -> state.heroProgress() } to heroProgress
        )

    private val standard: TargetUiState = TargetUiState(
        positionAlignment = PositionAlignment.Target(Center),
        aspectRatio = AspectRatio.Target(0.75f),
        height = Height.Target(0.5f),
        roundedCorners = RoundedCorners.Target(5),
    )

    private val heroElement: TargetUiState = TargetUiState(
        positionAlignment = PositionAlignment.Target(TopEnd),
        positionOffset = PositionOffset.Target(DpOffset(100.dp, (-150).dp)),
        aspectRatio = AspectRatio.Target(1f),
        height = Height.Target(1f),
        scale = Scale.Target(1.65f),
        roundedCorners = RoundedCorners.Target(100),
    )

    private fun State<InteractionTarget>.heroProgress(): Float =
        when (mode) {
            LIST -> 0f
            HERO -> 1f
        }

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

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext, scrollX.renderValueFlow)
}

