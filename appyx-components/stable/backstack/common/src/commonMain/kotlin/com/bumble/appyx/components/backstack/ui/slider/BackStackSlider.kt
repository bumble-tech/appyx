package com.bumble.appyx.components.backstack.ui.slider

import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment.Companion.InContainer
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation

class BackStackSlider<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseVisualisation<InteractionTarget, BackStackModel.State<InteractionTarget>, TargetUiState, MutableUiState>(
    uiContext = uiContext,
) {

    private val visible: TargetUiState =
        TargetUiState(
            positionAlignment = PositionAlignment.Target(InContainer),
            alpha = Alpha.Target(1f),
        )

    private val fadeOut: TargetUiState =
        TargetUiState(
            positionAlignment = PositionAlignment.Target(InContainer),
            alpha = Alpha.Target(1f),
        )

    override fun BackStackModel.State<InteractionTarget>.toUiTargets(
    ): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        created.map { MatchedTargetUiState(it, visible.toOutsideRight()) } +
                listOf(active).map { MatchedTargetUiState(it, visible.toNoOffset()) } +
                stashed.mapIndexed { index, element ->
                    MatchedTargetUiState(
                        element,
                        visible.toOutsideLeft((index - stashed.size).toFloat())
                    )
                } +
                destroyed.mapIndexed { index, element ->
                    MatchedTargetUiState(
                        element,
                        fadeOut.toOutsideRight()
                    )
                }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableUiState(uiContext)
}
