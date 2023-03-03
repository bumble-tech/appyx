package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.output.BaseUiState
import com.bumble.appyx.interactions.core.ui.output.MatchedUiState
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Offset
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class BackStackSlider<InteractionTarget : Any>(
    private val uiContext: UiContext,
) : BaseMotionController<InteractionTarget, BackStackModel.State<InteractionTarget>, BackStackSlider.UiState>(
    scope = uiContext.coroutineScope,
) {
    private val width = uiContext.transitionBounds.widthDp

    override fun defaultUiState(): UiState =
        UiState(screenWidth = uiContext.transitionBounds.widthDp)

    data class UiState(
        val offset: Offset = Offset(DpOffset(0.dp, 0.dp)),
        val alpha: Alpha = Alpha(value = 1f),
        val offsetMultiplier: Int = 1,
        val screenWidth: Dp
    ) : BaseUiState(listOf(offset.isAnimating, alpha.isAnimating)),
        Animatable<UiState> {

        override fun isVisible() =
            alpha.value > 0.0f && offset.value.x < screenWidth && offset.value.x > -screenWidth

        override val modifier: Modifier
            get() = Modifier
                .then(offset.modifier)
                .then(alpha.modifier)

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: UiState,
            springSpec: SpringSpec<Float>,
        ) {
            // FIXME this should match the own animationSpec of the model (which can also be supplied
            //  from operation extension methods) rather than created here
            val animationSpec: SpringSpec<Float> = spring(
                stiffness = Spring.StiffnessVeryLow / 5,
                dampingRatio = Spring.DampingRatioLowBouncy,
            )
            val a1 = scope.async {
                offset.animateTo(
                    props.offset.value,
                    spring(animationSpec.dampingRatio, animationSpec.stiffness)
                ) { updateVisibilityState() }
            }
            val a2 = scope.async {
                alpha.animateTo(
                    props.alpha.value,
                    spring(animationSpec.dampingRatio, animationSpec.stiffness)
                ) { updateVisibilityState() }
            }
            awaitAll(a1, a2)
        }

        override suspend fun snapTo(scope: CoroutineScope, props: UiState) {
            scope.launch {
                offset.snapTo(props.offset.value)
                alpha.snapTo(props.alpha.value)
                updateVisibilityState()
            }
        }

        override fun lerpTo(scope: CoroutineScope, start: UiState, end: UiState, fraction: Float) {
            scope.launch {
                offset.lerpTo(start.offset, end.offset, fraction)
                alpha.lerpTo(start.alpha, end.alpha, fraction)
                updateVisibilityState()
            }
        }
    }

    private val outsideLeft = UiState(
        offset = Offset(DpOffset(-width, 0.dp)),
        screenWidth = width
    )

    private val outsideRight = UiState(
        offset = Offset(DpOffset(width, 0.dp)),
        screenWidth = width
    )

    private val noOffset = UiState(
        offset = Offset(DpOffset(0.dp, 0.dp)),
        screenWidth = width
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
