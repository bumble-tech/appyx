package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.state.BaseUiState
import com.bumble.appyx.interactions.core.ui.state.MatchedUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class BackStackSlider<InteractionTarget : Any>(
    uiContext: UiContext,
    override val clipToBounds: Boolean = true,
) : BaseMotionController<InteractionTarget, BackStackModel.State<InteractionTarget>, BackStackSlider.UiState>(
    uiContext = uiContext,
) {
    private val width = uiContext.transitionBounds.widthDp

    override fun defaultUiState(uiContext: UiContext, initialUiState: UiState?): UiState =
        UiState(
            clipToBounds = clipToBounds,
            uiContext = uiContext,
        )

    data class UiState(
        val clipToBounds: Boolean,
        val uiContext: UiContext,
        val offset: Position = Position(
            initialOffset = DpOffset(0.dp, 0.dp),
            bounds = uiContext.transitionBounds,
            clipToBounds = clipToBounds
        ),
        val alpha: Alpha = Alpha(value = 1f),
        val offsetMultiplier: Int = 1
    ) : BaseUiState<UiState>(
        motionProperties = listOf(offset, alpha),
        coroutineScope = uiContext.coroutineScope
    ) {

        override val modifier: Modifier
            get() = Modifier
                .then(offset.modifier)
                .then(alpha.modifier)

        override suspend fun animateTo(
            scope: CoroutineScope,
            uiState: UiState,
            springSpec: SpringSpec<Float>,
        ) {
            val a1 = scope.async {
                offset.animateTo(
                    uiState.offset.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness)
                )
            }
            val a2 = scope.async {
                alpha.animateTo(
                    uiState.alpha.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness)
                )
            }
            awaitAll(a1, a2)
        }

        override suspend fun snapTo(scope: CoroutineScope, uiState: UiState) {
            scope.launch {
                offset.snapTo(uiState.offset.value)
                alpha.snapTo(uiState.alpha.value)
            }
        }

        override fun lerpTo(scope: CoroutineScope, start: UiState, end: UiState, fraction: Float) {
            scope.launch {
                offset.lerpTo(start.offset, end.offset, fraction)
                alpha.lerpTo(start.alpha, end.alpha, fraction)
            }
        }
    }

    private val outsideLeft = UiState(
        uiContext = uiContext,
        offset = Position(initialOffset = DpOffset(-width, 0.dp)),
        clipToBounds = clipToBounds
    )

    private val outsideRight = UiState(
        uiContext = uiContext,
        offset = Position(initialOffset = DpOffset(width, 0.dp)),
        clipToBounds = clipToBounds
    )

    private val noOffset = UiState(
        uiContext = uiContext,
        offset = Position(initialOffset = DpOffset(0.dp, 0.dp)),
        clipToBounds = clipToBounds
    )


    override fun BackStackModel.State<InteractionTarget>.toUiState(): List<MatchedUiState<InteractionTarget, UiState>> =
        created.map { MatchedUiState(it, outsideRight) } +
                listOf(MatchedUiState(active, noOffset)) +
                stashed.mapIndexed { index, element ->
                    MatchedUiState(
                        element,
                        outsideLeft.copy(offsetMultiplier = index + 1)
                    )
                } +
                destroyed.map { element ->
                    MatchedUiState(
                        element,
                        outsideRight.copy(alpha = Alpha(0f))
                    )
                }


}
