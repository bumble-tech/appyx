package com.bumble.appyx.demos.navigation.component.spotlighthero.visualisation.default

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.demos.navigation.component.spotlighthero.visualisation.SpotlightHeroVisualisation
import com.bumble.appyx.demos.navigation.component.spotlighthero.visualisation.property.HeroProgress
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.ui.property.impl.AspectRatio
import com.bumble.appyx.interactions.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.ui.property.impl.Height
import com.bumble.appyx.interactions.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.ui.property.impl.Scale
import com.bumble.appyx.interactions.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.Center
import com.bumble.appyx.interactions.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.TopCenter
import com.bumble.appyx.interactions.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.TopEnd
import com.bumble.appyx.interactions.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.ui.property.impl.position.PositionOffset
import com.bumble.appyx.interactions.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation

class SpotlightHeroDefaultVisualisation<NavTarget : Any>(
    uiContext: UiContext,
    initialState: State<NavTarget>
) : SpotlightHeroVisualisation<NavTarget>,
    BaseVisualisation<NavTarget, State<NavTarget>, TargetUiState, MutableUiState>(
    uiContext = uiContext
) {
    private val scrollX = GenericFloatProperty(
        coroutineScope = uiContext.coroutineScope,
        target = GenericFloatProperty.Target(initialState.activeIndex)
    )
    override val heroProgress = HeroProgress(
        coroutineScope = uiContext.coroutineScope,
        target = GenericFloatProperty.Target(initialState.heroProgress())
    )
    override val viewpointDimensions: List<Pair<(State<NavTarget>) -> Float, GenericFloatProperty>> =
        listOf(
            { state: State<NavTarget> -> state.activeIndex } to scrollX,
            { state: State<NavTarget> -> state.heroProgress() } to heroProgress
        )

    private val backdropStandard: TargetUiState = TargetUiState(
        positionAlignment = PositionAlignment.Target(Center),
        aspectRatio = AspectRatio.Target(0.75f),
        height = Height.Target(0.5f),
        roundedCorners = RoundedCorners.Target(5),
    )

    private val backdropHero: TargetUiState = TargetUiState(
        positionAlignment = PositionAlignment.Target(TopEnd),
        positionOffset = PositionOffset.Target(DpOffset(100.dp, (-150).dp)),
        aspectRatio = AspectRatio.Target(1f),
        height = Height.Target(1f),
        scale = Scale.Target(1.65f),
        roundedCorners = RoundedCorners.Target(100),
    )

    private val mainStandard: TargetUiState = TargetUiState(
        addScaleEffect = true,
        positionAlignment = PositionAlignment.Target(Center),
    )

    private val mainHero: TargetUiState = TargetUiState(
        addScaleEffect = true,
        positionAlignment = PositionAlignment.Target(TopCenter),
        positionOffset = PositionOffset.Target(DpOffset(0.dp, 40.dp)),
    )

    private fun State<NavTarget>.heroProgress(): Float =
        when (mode) {
            LIST -> 0f
            HERO -> 1f
        }

    override fun State<NavTarget>.toUiTargets(): List<MatchedTargetUiState<NavTarget, TargetUiState>> =
        positions.flatMapIndexed { index, position ->
            listOf(
                MatchedTargetUiState(
                    element = position.backdrop,
                    targetUiState = TargetUiState(
                        base = when (mode) {
                            LIST -> backdropStandard
                            HERO -> when (index.toFloat()) {
                                activeIndex -> backdropHero
                                else -> backdropStandard
                            }
                        },
                        positionInList = index
                    )
                ),
                MatchedTargetUiState(
                    element = position.main,
                    targetUiState = TargetUiState(
                        base = when (mode) {
                            LIST -> mainStandard
                            HERO -> when (index.toFloat()) {
                                activeIndex -> mainHero
                                else -> mainStandard
                            }
                        },
                        positionInList = index
                    )
                )
            )
        }


    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableUiState(uiContext, scrollX.renderValueFlow)
}

