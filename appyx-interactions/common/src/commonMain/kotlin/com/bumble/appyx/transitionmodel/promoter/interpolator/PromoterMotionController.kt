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
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.transition.Segment
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.interactions.core.ui.*
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.output.BaseUiState
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.interactions.core.ui.output.MatchedUiState
import com.bumble.appyx.transitionmodel.promoter.PromoterModel
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Suppress("TransitionPropertiesLabel")
class PromoterMotionController<InteractionTarget : Any>(
    childSize: Dp,
    transitionBounds: TransitionBounds
) : MotionController<InteractionTarget, PromoterModel.State<InteractionTarget>> {
    private val halfWidthDp = (transitionBounds.widthDp.value - childSize.value) / 2
    private val halfHeightDp = (transitionBounds.heightDp.value - childSize.value) / 2
    private val radiusDp = min(halfWidthDp, halfHeightDp) * 1.5f

    override val finishedAnimations: Flow<Element<InteractionTarget>>
        get() = TODO("Not yet implemented")

    // TODO migrate to BaseMotionController
    data class UiState(
        val dpOffset: DpOffset,
        val scale: Float,
        val angleDegrees: Float,
        val effectiveRadiusRatio: Float,
        val rotationY: Float,
        val rotationZ: Float,
    ) : BaseUiState(listOf()) {
        override fun isVisible() = true

    }

    private val created = UiState(
        dpOffset = DpOffset(0.dp, 0.dp),
        scale = 0f,
        angleDegrees = 0f,
        effectiveRadiusRatio = 1f,
        rotationY = 0f,
        rotationZ = 0f,
    )

    private val stage1 = created.copy(
        scale = 0.25f,
    )

    private val stage2 = stage1.copy(
        scale = 0.45f,
        angleDegrees = 90f,
    )

    private val stage3 = stage2.copy(
        scale = 0.65f,
        angleDegrees = 180f
    )

    private val stage4 = stage3.copy(
        scale = 0.85f,
        angleDegrees = 270f,
    )

    private val stage5 = stage4.copy(
        scale = 1f,
        effectiveRadiusRatio = 0f,
        rotationY = 360f,
    )

    private val destroyed = stage5.copy(
        dpOffset = DpOffset(500.dp, (-200).dp),
        scale = 0f,
        rotationZ = 540f
    )

    // TODO Migrate to BaseMotionController

    private fun <InteractionTarget : Any> PromoterModel.State<InteractionTarget>.toProps(): List<MatchedUiState<InteractionTarget, UiState>> =
        elements.map {
            MatchedUiState(
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
        segment: Segment<PromoterModel.State<InteractionTarget>>,
        segmentProgress: Flow<Float>,
        initialProgress: Float
    ): List<ElementUiModel<InteractionTarget>> {
        val (fromState, targetState) = segment.stateTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!

            ElementUiModel(
                // TODO fix after migration to base interoplator
                element = t1.element,
                visibleState = MutableStateFlow(value = true),
                animationContainer = {},
                modifier = Modifier.composed {
                    val segmentProgress by segmentProgress.collectAsState(initialProgress)
                    val angleRadians0 = Math.toRadians(t0.uiState.angleDegrees.toDouble() - 90)
                    val angleRadians1 = Math.toRadians(t1.uiState.angleDegrees.toDouble() - 90)

                    // Lerp block
                    val dpOffsetX =
                        lerpFloat(
                            t0.uiState.dpOffset.x.value,
                            t1.uiState.dpOffset.x.value,
                            segmentProgress
                        )
                    val dpOffsetY =
                        lerpFloat(
                            t0.uiState.dpOffset.y.value,
                            t1.uiState.dpOffset.y.value,
                            segmentProgress
                        )
                    val rotationY =
                        lerpFloat(t0.uiState.rotationY, t1.uiState.rotationY, segmentProgress)
                    val rotationZ =
                        lerpFloat(t0.uiState.rotationZ, t1.uiState.rotationZ, segmentProgress)
                    val scale = lerpFloat(t0.uiState.scale, t1.uiState.scale, segmentProgress)
                    val angleRadians =
                        lerpFloat(angleRadians0.toFloat(), angleRadians1.toFloat(), segmentProgress)
                    val effectiveRadiusRatio = lerpFloat(
                        t0.uiState.effectiveRadiusRatio,
                        t1.uiState.effectiveRadiusRatio,
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
                progress = segmentProgress
            )
        }
    }

    override fun mapUpdate(
        update: Update<PromoterModel.State<InteractionTarget>>
    ): List<ElementUiModel<InteractionTarget>> {
        TODO("Not yet implemented")
    }
}
