package com.bumble.appyx.v2.app.node.teaser.promoter.transitionhandler

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import com.bumble.appyx.v2.app.node.teaser.promoter.routingsource.Promoter
import com.bumble.appyx.v2.core.routing.transition.ModifierTransitionHandler
import com.bumble.appyx.v2.core.routing.transition.TransitionDescriptor
import com.bumble.appyx.v2.core.routing.transition.TransitionSpec
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Suppress("TransitionPropertiesLabel")
class PromoterTransitionHandler<T>(
    private val transitionSpec: TransitionSpec<Promoter.TransitionState, Float> = { tween(500) }
) : ModifierTransitionHandler<T, Promoter.TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Promoter.TransitionState>,
        descriptor: TransitionDescriptor<T, Promoter.TransitionState>
    ): Modifier = modifier.composed {

        // TODO / 2 ?
        val halfWidth = descriptor.params.bounds.width.value
        val halfHeight = descriptor.params.bounds.height.value
        val centered = IntOffset(halfWidth.roundToInt(), halfHeight.roundToInt())

        val radius = min(halfWidth, halfHeight)

        val angleDegrees = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Promoter.TransitionState.CREATED -> 0f
                    Promoter.TransitionState.STAGE1 -> 0f
                    Promoter.TransitionState.STAGE2 -> 90f
                    Promoter.TransitionState.STAGE3 -> 180f
                    Promoter.TransitionState.STAGE4 -> 270f
                    Promoter.TransitionState.SELECTED -> 270f
                    Promoter.TransitionState.DESTROYED -> 270f
                }
            })

        val effectiveRadius = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Promoter.TransitionState.CREATED -> radius
                    Promoter.TransitionState.STAGE1 -> radius
                    Promoter.TransitionState.STAGE2 -> radius
                    Promoter.TransitionState.STAGE3 -> radius
                    Promoter.TransitionState.STAGE4 -> radius
                    Promoter.TransitionState.SELECTED -> 0f
                    Promoter.TransitionState.DESTROYED -> 0f
                }
            })

        val arcOffset = derivedStateOf {
            val angleRadians = Math.toRadians(angleDegrees.value.toDouble() - 90)
            val x = effectiveRadius.value * cos(angleRadians)
            val y = effectiveRadius.value * sin(angleRadians)
            IntOffset(x.roundToInt(), y.roundToInt())
        }

        val rotationY = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Promoter.TransitionState.CREATED -> 0f
                    Promoter.TransitionState.STAGE1 -> 0f
                    Promoter.TransitionState.STAGE2 -> 0f
                    Promoter.TransitionState.STAGE3 -> 0f
                    Promoter.TransitionState.STAGE4 -> 0f
                    Promoter.TransitionState.SELECTED -> 360f
                    Promoter.TransitionState.DESTROYED -> 360f
                }
            })

        val scale = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Promoter.TransitionState.CREATED -> 0f
                    Promoter.TransitionState.STAGE1 -> 0.25f
                    Promoter.TransitionState.STAGE2 -> 0.45f
                    Promoter.TransitionState.STAGE3 -> 0.65f
                    Promoter.TransitionState.STAGE4 -> 0.85f
                    Promoter.TransitionState.SELECTED -> 1f
                    Promoter.TransitionState.DESTROYED -> 0f
                }
            })

        val destroyProgress = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Promoter.TransitionState.DESTROYED -> 1f
                    else -> 0f
                }
            })

        return@composed this
            .offset {
                centered +
                    arcOffset.value +
                    IntOffset(
                        x = (500f * destroyProgress.value * this.density).roundToInt(),
                        y = (-200 * destroyProgress.value * this.density).roundToInt()
                    )
            }
            .graphicsLayer(
                rotationY = rotationY.value,
                rotationZ = 540 * destroyProgress.value
            )
            .scale(scale.value)
    }
}

@Composable
fun <T> rememberPromoterTransitionHandler(
    transitionSpec: TransitionSpec<Promoter.TransitionState, Float> = { tween(500) }
): ModifierTransitionHandler<T, Promoter.TransitionState> = remember {
    PromoterTransitionHandler(transitionSpec)
}
