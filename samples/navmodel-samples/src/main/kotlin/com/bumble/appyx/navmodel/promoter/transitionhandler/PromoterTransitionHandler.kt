package com.bumble.appyx.navmodel.promoter.transitionhandler

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Suppress("TransitionPropertiesLabel")
class PromoterTransitionHandler<T : Parcelable>(
    private val childSize: Dp,
    private val transitionSpec: TransitionSpec<Promoter.State, Float> = { tween(500) }
) : ModifierTransitionHandler<T, Promoter.State>() {

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

    @SuppressLint("UnusedTransitionTargetStateParameter", "ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Promoter.State>,
        descriptor: TransitionDescriptor<T, Promoter.State>
    ): Modifier = modifier.composed {

        val halfWidthDp = (descriptor.params.bounds.width.value - childSize.value) / 2
        val halfHeightDp = (descriptor.params.bounds.height.value - childSize.value) / 2
        val radiusDp = min(halfWidthDp, halfHeightDp) // * 0.75f

        val angleDegrees by transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().angleDegrees }
        )

        val effectiveRadiusDp by transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().effectiveRadiusRatio * radiusDp }
        )

        val arcOffsetDp by remember {
            derivedStateOf {
                val angleRadians = Math.toRadians(angleDegrees.toDouble() - 90)
                val x = effectiveRadiusDp * cos(angleRadians)
                val y = effectiveRadiusDp * sin(angleRadians)
                Offset(x.toFloat(), y.toFloat())
            }
        }

        val rotationY by transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().rotationY })

        val rotationZ by transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().rotationZ })

        val scale by transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().scale })

        val dpOffsetX by transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().dpOffset.x.value })

        val dpOffsetY by transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = { it.target().dpOffset.y.value })

        return@composed this
            .offset {
                IntOffset(
                    x = (this.density * (halfWidthDp + dpOffsetX + arcOffsetDp.x)).roundToInt(),
                    y = (this.density * (halfHeightDp + dpOffsetY + arcOffsetDp.y)).roundToInt()
                )
            }
            .graphicsLayer(
                rotationY = rotationY,
                rotationZ = rotationZ
            )
            .scale(scale)
    }

    @Composable
    private fun Promoter.State.target() =
        when (this) {
            Promoter.State.CREATED -> created
            Promoter.State.STAGE1 -> stage1
            Promoter.State.STAGE2 -> stage2
            Promoter.State.STAGE3 -> stage3
            Promoter.State.STAGE4 -> stage4
            Promoter.State.SELECTED -> selected
            Promoter.State.DESTROYED -> destroyed
        }
}

@Composable
fun <T : Parcelable> rememberPromoterTransitionHandler(
    childSize: Dp,
    transitionSpec: TransitionSpec<Promoter.State, Float> = { tween(500) }
): ModifierTransitionHandler<T, Promoter.State> = remember {
    PromoterTransitionHandler(childSize, transitionSpec)
}
