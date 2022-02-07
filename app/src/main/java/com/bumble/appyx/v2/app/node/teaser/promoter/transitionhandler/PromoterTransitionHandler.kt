package com.bumble.appyx.v2.app.node.teaser.promoter.transitionhandler

import android.annotation.SuppressLint
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
    private val childSize: Dp,
    private val transitionSpec: TransitionSpec<Promoter.TransitionState, Float> = { tween(500) }
) : ModifierTransitionHandler<T, Promoter.TransitionState>() {

    data class TargetStateValues(
        val dpOffset: DpOffset,
        val scale: Float,
        val angleDegrees: Float,
        val effectiveRadiusRatio: Float,
        val rotationY: Float,
        val rotationZ: Float,
    )

    private val created = TargetStateValues(
        dpOffset = DpOffset(0.dp, 0.dp),
        scale = 0f,
        angleDegrees = 0f,
        effectiveRadiusRatio = 1f,
        rotationY = 0f,
        rotationZ = 0f,
    )

    private val stage1 = created.copy(
        scale = 0.25f
    )

    private val stage2 = stage1.copy(
        scale = 0.45f,
        angleDegrees = 90f,
    )

    private val stage3 = stage2.copy(
        scale = 0.65f,
        angleDegrees = 180f,
    )

    private val stage4 = stage3.copy(
        scale = 0.85f,
        angleDegrees = 270f,
    )

    private val selected = stage4.copy(
        scale = 1f,
        effectiveRadiusRatio = 0f,
        rotationY = 360f,
    )

    private val destroyed = selected.copy(
        dpOffset = DpOffset(500.dp, (-200).dp),
        scale = 0f,
        rotationZ = 540f,
    )

    @SuppressLint("UnusedTransitionTargetStateParameter")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Promoter.TransitionState>,
        descriptor: TransitionDescriptor<T, Promoter.TransitionState>
    ): Modifier = modifier.composed {

        val halfWidthDp = (descriptor.params.bounds.width.value - childSize.value) / 2
        val halfHeightDp = (descriptor.params.bounds.height.value - childSize.value) / 2
        val radiusDp = min(halfWidthDp, halfHeightDp) // * 0.75f

        val angleDegrees = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().angleDegrees }
        )

        val effectiveRadiusDp = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().effectiveRadiusRatio * radiusDp }
        )

        val arcOffsetDp = derivedStateOf {
            val angleRadians = Math.toRadians(angleDegrees.value.toDouble() - 90)
            val x = effectiveRadiusDp.value * cos(angleRadians)
            val y = effectiveRadiusDp.value * sin(angleRadians)
            Offset(x.toFloat(), y.toFloat())
        }

        val rotationY = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().rotationY })

        val rotationZ = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().rotationZ })

        val scale = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().scale })

        val dpOffsetX = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().dpOffset.x.value })

        val dpOffsetY = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().dpOffset.y.value })

        return@composed this
            .offset {
                IntOffset(
                    x = (this.density * (halfWidthDp + dpOffsetX.value + arcOffsetDp.value.x)).roundToInt(),
                    y = (this.density * (halfHeightDp + dpOffsetY.value + arcOffsetDp.value.y)).roundToInt()
                )
            }
            .graphicsLayer(
                rotationY = rotationY.value,
                rotationZ = rotationZ.value
            )
            .scale(scale.value)
    }

    @Composable
    private fun Promoter.TransitionState.target() =
        when (this) {
            Promoter.TransitionState.CREATED -> created
            Promoter.TransitionState.STAGE1 -> stage1
            Promoter.TransitionState.STAGE2 -> stage2
            Promoter.TransitionState.STAGE3 -> stage3
            Promoter.TransitionState.STAGE4 -> stage4
            Promoter.TransitionState.SELECTED -> selected
            Promoter.TransitionState.DESTROYED -> destroyed
        }
}

@Composable
fun <T> rememberPromoterTransitionHandler(
    childSize: Dp,
    transitionSpec: TransitionSpec<Promoter.TransitionState, Float> = { tween(500) }
): ModifierTransitionHandler<T, Promoter.TransitionState> = remember {
    PromoterTransitionHandler(childSize, transitionSpec)
}
