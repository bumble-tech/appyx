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
import com.bumble.appyx.core.navigation2.NavModel.Segment
import com.bumble.appyx.core.navigation2.ui.RenderParams
import com.bumble.appyx.core.navigation2.ui.UiProps
import com.bumble.appyx.core.navigation2.ui.UiProps.Companion.lerpFloat
import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter.State
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Suppress("TransitionPropertiesLabel")
class PromoterProps<NavTarget>(
    private val childSize: Dp,
    transitionParams: TransitionParams
) : UiProps<NavTarget, State> {
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

    private fun State.toProps() =
        when (this) {
            State.CREATED -> created
            State.STAGE1 -> stage1
            State.STAGE2 -> stage2
            State.STAGE3 -> stage3
            State.STAGE4 -> stage4
            State.SELECTED -> selected
            State.DESTROYED -> destroyed
        }

    override fun map(segment: Segment<NavTarget, State>): List<RenderParams<NavTarget, State>> {
        val (fromState, targetState) = segment.navTransition

        return targetState.map { t1 ->
            val t0 = fromState.find { it.key == t1.key }!!
            val props0 = t0.state.toProps()
            val props1 = t1.state.toProps()

            // TODO memoize
            val angleRadians0 = Math.toRadians(props0.angleDegrees.toDouble() - 90)
            val angleRadians1 = Math.toRadians(props1.angleDegrees.toDouble() - 90)

            // Lerp block
            val dpOffsetX =
                lerpFloat(props0.dpOffset.x.value, props1.dpOffset.x.value, segment.progress)
            val dpOffsetY =
                lerpFloat(props0.dpOffset.y.value, props1.dpOffset.y.value, segment.progress)
            val rotationY = lerpFloat(props0.rotationY, props1.rotationY, segment.progress)
            val rotationZ = lerpFloat(props0.rotationZ, props1.rotationZ, segment.progress)
            val scale = lerpFloat(props0.scale, props1.scale, segment.progress)
            val angleRadians =
                lerpFloat(angleRadians0.toFloat(), angleRadians1.toFloat(), segment.progress)
            val effectiveRadiusRatio = lerpFloat(
                props0.effectiveRadiusRatio,
                props1.effectiveRadiusRatio,
                segment.progress
            )
            val effectiveRadius = radiusDp * effectiveRadiusRatio
            val x = (effectiveRadius * cos(angleRadians))
            val y = (effectiveRadius * sin(angleRadians))
            val arcOffsetDp = Offset(x, y)

            RenderParams(
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
