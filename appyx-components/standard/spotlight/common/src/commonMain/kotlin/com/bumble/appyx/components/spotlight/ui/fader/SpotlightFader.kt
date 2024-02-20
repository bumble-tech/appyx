package com.bumble.appyx.components.spotlight.ui.fader

import androidx.compose.animation.core.SpringSpec
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.ui.DefaultAnimationSpec
import com.bumble.appyx.interactions.ui.property.impl.Alpha
import com.bumble.appyx.interactions.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation


class SpotlightFader<NavTarget : Any>(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseVisualisation<NavTarget, SpotlightModel.State<NavTarget>, TargetUiState, MutableUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec
) {
    private val hidden: TargetUiState = TargetUiState(
        alpha = Alpha.Target(0f),
    )

    private val visible: TargetUiState = TargetUiState(
        alpha = Alpha.Target(1f),
    )

    override fun SpotlightModel.State<NavTarget>.toUiTargets():
            List<MatchedTargetUiState<NavTarget, TargetUiState>> {
        return positions.flatMapIndexed { index, position ->
            position.elements.map {
                MatchedTargetUiState(
                    element = it.key,
                    targetUiState = TargetUiState(
                        base = if (index == activeIndex.toInt()) {
                            when (it.value) {
                                CREATED -> hidden
                                STANDARD -> visible
                                DESTROYED -> hidden
                            }
                        } else {
                            hidden
                        }
                    )
                )
            }
        }
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableUiState(uiContext)
}

