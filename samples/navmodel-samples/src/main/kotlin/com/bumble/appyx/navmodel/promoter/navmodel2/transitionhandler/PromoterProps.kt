package com.bumble.appyx.navmodel.promoter.navmodel2.transitionhandler

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.navigation.transition.TransitionParams
import com.bumble.appyx.core.navigation2.NavModel
import com.bumble.appyx.core.navigation2.ui.Modifiers
import com.bumble.appyx.core.navigation2.ui.UiProps
import com.bumble.appyx.core.navigation2.ui.UiProps.Companion.lerp
import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Suppress("TransitionPropertiesLabel")
class PromoterProps<NavTarget>(
    private val childSize: Dp,
    transitionParams: TransitionParams
) : UiProps<NavTarget, Promoter.State> {
    private val halfWidthDp = (transitionParams.bounds.width.value - childSize.value) / 2
    private val halfHeightDp = (transitionParams.bounds.height.value - childSize.value) / 2
    private val radiusDp = min(halfWidthDp, halfHeightDp) * 1.5f

    data class Props(
        val dpOffset: DpOffset,
        val scale: Float,
        val angleDegrees: Float,
        val effectiveRadiusRatio: Float,
        val rotationY: Float,
        val rotationZ: Float,
    )

    private val created = Props(
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

    private fun Promoter.State.toProps() =
        when (this) {
            Promoter.State.CREATED -> created
            Promoter.State.STAGE1 -> stage1
            Promoter.State.STAGE2 -> stage2
            Promoter.State.STAGE3 -> stage3
            Promoter.State.STAGE4 -> stage4
            Promoter.State.SELECTED -> selected
            Promoter.State.DESTROYED -> destroyed
        }

    override fun map(segment: NavModel.Segment<NavTarget, Promoter.State>): List<Modifiers<NavTarget, Promoter.State>> {
        val (fromState, targetState) = segment.navTransition

        return targetState.map { t1 ->
            val t0 = fromState.find { it.key == t1.key }!!
            val props0 = t0.state.toProps()
            val props1 = t1.state.toProps()

            // TODO memoize
            val angleRadians0 = Math.toRadians(props0.angleDegrees.toDouble() - 90)
            val angleRadians1 = Math.toRadians(props1.angleDegrees.toDouble() - 90)

            // Lerp block
            val dpOffsetX = lerp(props0.dpOffset.x.value, props1.dpOffset.x.value, segment.progress)
            val dpOffsetY = lerp(props0.dpOffset.y.value, props1.dpOffset.y.value, segment.progress)
            val rotationY = lerp(props0.rotationY, props1.rotationY, segment.progress)
            val rotationZ = lerp(props0.rotationZ, props1.rotationZ, segment.progress)
            val scale = lerp(props0.scale, props1.scale, segment.progress)
            val angleRadians = lerp(angleRadians0.toFloat(), angleRadians1.toFloat(), segment.progress)
            val effectiveRadiusRatio = lerp(props0.effectiveRadiusRatio, props1.effectiveRadiusRatio, segment.progress)
            val effectiveRadius = radiusDp * effectiveRadiusRatio
            val x = (effectiveRadius * cos(angleRadians))
            val y = (effectiveRadius * sin(angleRadians))
            val arcOffsetDp = Offset(x, y)

            Modifiers(
                navElement = t1,
                modifier = Modifier
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
            )
        }
    }
}
