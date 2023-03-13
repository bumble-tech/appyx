package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.state.BaseUiState
import com.bumble.appyx.interactions.core.ui.state.MatchedUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BackStackCrossfader<InteractionTarget : Any>(
    uiContext: UiContext
) : BaseMotionController<InteractionTarget, BackStackModel.State<InteractionTarget>, BackStackCrossfader.UiState>(
    uiContext = uiContext
) {

    override fun defaultUiState(uiContext: UiContext): UiState = UiState(uiContext)

    class UiState(
        val uiContext: UiContext,
        val alpha: Alpha = Alpha(value = 1f),
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
            val a1 = scope.async {
                alpha.animateTo(
                    uiState.alpha.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness)
                )
            }
            a1.await()
        }

        override fun lerpTo(scope: CoroutineScope, start: UiState, end: UiState, fraction: Float) {
            scope.launch {
                alpha.lerpTo(start.alpha, end.alpha, fraction)
            }
        }

    }

    private val visible = UiState(
        uiContext = uiContext,
        alpha = Alpha(value = 1f)
    )

    private val hidden = UiState(
        uiContext = uiContext,
        alpha = Alpha(value = 0f)
    )

    override fun BackStackModel.State<InteractionTarget>.toUiState(): List<MatchedUiState<InteractionTarget, UiState>> =
        listOf(
            MatchedUiState(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedUiState(it, hidden)
        }
}
