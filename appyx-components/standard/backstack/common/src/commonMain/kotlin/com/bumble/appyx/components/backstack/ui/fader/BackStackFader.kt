package com.bumble.appyx.components.backstack.ui.fader

import androidx.compose.animation.core.SpringSpec
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.ui.DefaultAnimationSpec
import com.bumble.appyx.interactions.ui.property.impl.Alpha
import com.bumble.appyx.interactions.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation

class BackStackFader<NavTarget : Any>(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseVisualisation<NavTarget, BackStackModel.State<NavTarget>, TargetUiState, MutableUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec,
) {
    private val visible = TargetUiState(
        alpha = Alpha.Target(1f)
    )

    private val hidden = TargetUiState(
        alpha = Alpha.Target(0f)
    )

    override fun BackStackModel.State<NavTarget>.toUiTargets():
            List<MatchedTargetUiState<NavTarget, TargetUiState>> =
        listOf(
            MatchedTargetUiState(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedTargetUiState(it, hidden)
        }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableUiState(uiContext)
}
