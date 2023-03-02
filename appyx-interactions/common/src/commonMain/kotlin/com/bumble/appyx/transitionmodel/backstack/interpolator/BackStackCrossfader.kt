package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.output.BaseUiState
import com.bumble.appyx.interactions.core.ui.output.MatchedUiState
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BackStackCrossfader<InteractionTarget : Any>(
    scope: CoroutineScope
) : BaseMotionController<InteractionTarget, BackStackModel.State<InteractionTarget>, BackStackCrossfader.UiState>(
    scope = scope
) {

    override fun defaultUiState(): UiState = UiState()

    class UiState(
        val alpha: Alpha = Alpha(value = 1f),
    ) : BaseUiState(listOf(alpha.isAnimating)), HasModifier, Animatable<UiState> {

        override val modifier: Modifier
            get() = Modifier
                .then(alpha.modifier)

        override suspend fun snapTo(scope: CoroutineScope, props: UiState) {
            scope.launch {
                alpha.snapTo(props.alpha.value)
                updateVisibilityState()
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: UiState,
            springSpec: SpringSpec<Float>,
        ) {
            val a1 = scope.async {
                alpha.animateTo(
                    props.alpha.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness)
                ) {
                    updateVisibilityState()
                }
            }
            a1.await()
        }

        override fun lerpTo(scope: CoroutineScope, start: UiState, end: UiState, fraction: Float) {
            scope.launch {
                alpha.lerpTo(start.alpha, end.alpha, fraction)
                updateVisibilityState()
            }
        }

        override fun isVisible() = alpha.value > 0.0f

    }

    private val visible = UiState(
        alpha = Alpha(value = 1f)
    )

    private val hidden = UiState(
        alpha = Alpha(value = 0f)
    )

    override fun BackStackModel.State<InteractionTarget>.toUiState(): List<MatchedUiState<InteractionTarget, UiState>> =
        listOf(
            MatchedUiState(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedUiState(it, hidden)
        }
}
