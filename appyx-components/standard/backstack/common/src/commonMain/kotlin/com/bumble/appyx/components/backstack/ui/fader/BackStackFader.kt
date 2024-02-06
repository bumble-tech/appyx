package com.bumble.appyx.components.backstack.ui.fader

import androidx.compose.animation.core.SpringSpec
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.DefaultAnimationSpec
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation

class BackStackFader<InteractionTarget : Any>(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseVisualisation<InteractionTarget, BackStackModel.State<InteractionTarget>, TargetUiState, MutableUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec,
) {
    private val visible = TargetUiState(
        alpha = Alpha.Target(1f)
    )

    private val hidden = TargetUiState(
        alpha = Alpha.Target(0f)
    )

    override fun BackStackModel.State<InteractionTarget>.toUiTargets():
            List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        listOf(
            MatchedTargetUiState(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedTargetUiState(it, hidden)
        }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableUiState(uiContext)
}
