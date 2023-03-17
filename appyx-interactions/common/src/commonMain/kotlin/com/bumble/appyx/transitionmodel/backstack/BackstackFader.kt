package com.bumble.appyx.transitionmodel.backstack

import DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.state.BaseUiState
import com.bumble.appyx.interactions.core.ui.state.MatchedUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BackstackFader<InteractionTarget : Any>(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<InteractionTarget, BackStackModel.State<InteractionTarget>, BackstackFader.UiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec,
) {
    override fun defaultUiState(uiContext: UiContext, initialUiState: UiState?): UiState = UiState(uiContext)

    class UiState(
        val uiContext: UiContext,
        var alpha: Alpha = Alpha(1f),
    ) : BaseUiState<UiState>(
        motionProperties = listOf(alpha),
        coroutineScope = uiContext.coroutineScope
    ) {

        override val modifier: Modifier
            get() = Modifier
                .then(alpha.modifier)

        override suspend fun snapTo(scope: CoroutineScope, uiState: UiState) {
            scope.launch {
                alpha.snapTo(uiState.alpha.value)
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            uiState: UiState,
            springSpec: SpringSpec<Float>,
        ) {
            scope.launch {
                alpha.animateTo(uiState.alpha.value, springSpec)
            }
        }

        override fun lerpTo(scope: CoroutineScope, start: UiState, end: UiState, fraction: Float) {
            scope.launch {
                alpha.lerpTo(start.alpha, end.alpha, fraction)
            }
        }
    }

    private val visible = UiState(
        uiContext = uiContext,
        alpha = Alpha(1f)
    )

    private val hidden = UiState(
        uiContext = uiContext,
        alpha = Alpha(0f)
    )

    override fun BackStackModel.State<InteractionTarget>.toUiState(): List<MatchedUiState<InteractionTarget, UiState>> =
        listOf(
            MatchedUiState(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedUiState(it, hidden)
        }
}
