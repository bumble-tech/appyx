package com.bumble.appyx.components.spotlight.ui.fader

import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class SpotlightFader<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseMotionController<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext
) {
    private val created: TargetUiState = TargetUiState(
        alpha = Alpha.Target(0f),
    )

    private val active: TargetUiState = TargetUiState(
        alpha = Alpha.Target(1f),
    )

    private val inactive: TargetUiState = TargetUiState(
        alpha = Alpha.Target(0f),
    )

    private val destroyed: TargetUiState = TargetUiState(
        alpha = Alpha.Target(0f),
    )

    override fun State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> {
        return positions.flatMapIndexed { index, position ->
            position.elements.map {
                MatchedTargetUiState(
                    element = it.key,
                    targetUiState = TargetUiState(
                        base = when (it.value) {
                            CREATED -> created
                            STANDARD -> if (index == activeIndex.toInt()) {
                                active
                            } else {
                                inactive
                            }
                            DESTROYED -> destroyed
                        }
                    )
                )
            }
        }
    }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableState(uiContext)
}
