package com.bumble.appyx.transitionmodel.promoter.interpolator

import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.Segment
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.ui.*
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.transitionmodel.promoter.PromoterModel
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState
import kotlinx.coroutines.flow.Flow
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Suppress("TransitionPropertiesLabel")
class PromoterInterpolator<NavTarget : Any>(
    childSize: Dp,
    transitionBounds: TransitionBounds
) : Interpolator<NavTarget, PromoterModel.State<NavTarget>> {
    private val halfWidthDp = (transitionBounds.widthDp.value - childSize.value) / 2
    private val halfHeightDp = (transitionBounds.heightDp.value - childSize.value) / 2
    private val radiusDp = min(halfWidthDp, halfHeightDp) * 1.5f

    data class Props(
        val dpOffset: DpOffset,
        val scale: Float,
        val angleDegrees: Float,
        val effectiveRadiusRatio: Float,
        val rotationY: Float,
        val rotationZ: Float,
        override val isVisible: Boolean
    ) : BaseProps

    private val created = Props(
        dpOffset = DpOffset(0.dp, 0.dp),
        scale = 0f,
        angleDegrees = 0f,
        effectiveRadiusRatio = 1f,
        rotationY = 0f,
        rotationZ = 0f,
        isVisible = false
    )

    private val stage1 = created.copy(
        scale = 0.25f,
        isVisible = true
    )

    private val stage2 = stage1.copy(
        scale = 0.45f,
        angleDegrees = 90f,
        isVisible = true
    )

    private val stage3 = stage2.copy(
        scale = 0.65f,
        angleDegrees = 180f,
        isVisible = true
    )

    private val stage4 = stage3.copy(
        scale = 0.85f,
        angleDegrees = 270f,
        isVisible = true
    )

    private val stage5 = stage4.copy(
        scale = 1f,
        effectiveRadiusRatio = 0f,
        rotationY = 360f,
        isVisible = true
    )

    private val destroyed = stage5.copy(
        dpOffset = DpOffset(500.dp, (-200).dp),
        scale = 0f,
        rotationZ = 540f,
        isVisible = false
    )

    private fun <NavTarget : Any> PromoterModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        elements.map {
            MatchedProps(
                it.first, when (it.second) {
                    ElementState.CREATED -> created
                    ElementState.STAGE1 -> stage1
                    ElementState.STAGE2 -> stage2
                    ElementState.STAGE3 -> stage3
                    ElementState.STAGE4 -> stage4
                    ElementState.STAGE5 -> stage5
                    else -> destroyed
                }
            )
        }

    override fun mapSegment(
        segment: Segment<PromoterModel.State<NavTarget>>,
        segmentProgress: Flow<Float>
    ): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!

            // TODO memoize
            val angleRadians0 = Math.toRadians(t0.props.angleDegrees.toDouble() - 90)
            val angleRadians1 = Math.toRadians(t1.props.angleDegrees.toDouble() - 90)

            // Lerp block


            FrameModel(
                navElement = t1.element,
                modifier = Modifier
                    .composed {
                        val segmentProgress by segmentProgress.collectAsState(0f)
                        val dpOffsetX =
                            lerpFloat(t0.props.dpOffset.x.value, t1.props.dpOffset.x.value, segmentProgress)
                        val dpOffsetY =
                            lerpFloat(t0.props.dpOffset.y.value, t1.props.dpOffset.y.value, segmentProgress)
                        val rotationY = lerpFloat(t0.props.rotationY, t1.props.rotationY, segmentProgress)
                        val rotationZ = lerpFloat(t0.props.rotationZ, t1.props.rotationZ, segmentProgress)
                        val scale = lerpFloat(t0.props.scale, t1.props.scale, segmentProgress)
                        val angleRadians =
                            lerpFloat(angleRadians0.toFloat(), angleRadians1.toFloat(), segmentProgress)
                        val effectiveRadiusRatio = lerpFloat(
                            t0.props.effectiveRadiusRatio,
                            t1.props.effectiveRadiusRatio,
                            segmentProgress
                        )
                        val effectiveRadius = radiusDp * effectiveRadiusRatio
                        val x = (effectiveRadius * cos(angleRadians))
                        val y = (effectiveRadius * sin(angleRadians))
                        val arcOffsetDp = Offset(x, y)
                        this.offset {
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
                    },
//                progress = segmentProgress,
                state = resolveNavElementVisibility(t0.props, t1.props)
            )
        }
    }

    override fun mapUpdate(update: Update<PromoterModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        TODO("Not yet implemented")
    }
}
