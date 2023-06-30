package com.bumble.appyx.components.experimental.promoter.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.unit.Dp
import com.bumble.appyx.components.experimental.cards.ui.MutableUiState
import com.bumble.appyx.components.experimental.cards.ui.TargetUiState
import com.bumble.appyx.components.experimental.promoter.PromoterModel
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

@Suppress("TransitionPropertiesLabel")
class PromoterMotionController<InteractionTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec,
    childSize: Dp,
) : BaseMotionController<InteractionTarget, PromoterModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = uiAnimationSpec,
) {
    override fun PromoterModel.State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> {
        TODO("Not yet implemented")
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState {
        TODO("Not yet implemented")
    }
    //    private val scope = uiContext.coroutineScope
//    private val halfWidthDp = (uiContext.transitionBounds.widthDp.value - childSize.value) / 2
//    private val halfHeightDp = (uiContext.transitionBounds.heightDp.value - childSize.value) / 2
//    private val radiusDp = min(halfWidthDp, halfHeightDp) * 1.5f
//
//    override val finishedAnimations: Flow<Element<InteractionTarget>>
//        get() = TODO("Not yet implemented")
//
//    override fun defaultUiState(uiContext: UiContext, initialUiState: MutableUiState?): MutableUiState = created.copy()
//
//    // TODO migrate fields to MotionProperty instances
//    data class MutableUiState(
//        val scope: CoroutineScope,
//        val dpOffset: DpOffset,
//        val scale: Float,
//        val angleDegrees: Float,
//        val effectiveRadiusRatio: Float,
//        val rotationY: Float,
//        val rotationZ: Float,
//    ) : BaseMutableUiState<MutableUiState>(listOf()) { // TODO
//
//        override val modifier: Modifier
//            get() = TODO("Not yet implemented")
//
//        override suspend fun snapTo(scope: CoroutineScope, uiState: MutableUiState) {
//            TODO("Not yet implemented")
//        }
//
//        override fun lerpTo(scope: CoroutineScope, start: MutableUiState, end: MutableUiState, fraction: Float) {
//            TODO("Not yet implemented")
//        }
//
//        override suspend fun animateTo(
//            scope: CoroutineScope,
//            target: MutableUiState,
//            springSpec: SpringSpec<Float>
//        ) {
//            TODO("Not yet implemented")
//        }
//    }
//
//    private val created = MutableUiState(
//        dpOffset = DpOffset(0.dp, 0.dp),
//        scale = 0f,
//        angleDegrees = 0f,
//        effectiveRadiusRatio = 1f,
//        rotationY = 0f,
//        rotationZ = 0f,
//        scope = scope
//    )
//
//    private val stage1 = created.copy(
//        scale = 0.25f,
//    )
//
//    private val stage2 = stage1.copy(
//        scale = 0.45f,
//        angleDegrees = 90f,
//    )
//
//    private val stage3 = stage2.copy(
//        scale = 0.65f,
//        angleDegrees = 180f
//    )
//
//    private val stage4 = stage3.copy(
//        scale = 0.85f,
//        angleDegrees = 270f,
//    )
//
//    private val stage5 = stage4.copy(
//        scale = 1f,
//        effectiveRadiusRatio = 0f,
//        rotationY = 360f,
//    )
//
//    private val destroyed = stage5.copy(
//        dpOffset = DpOffset(500.dp, (-200).dp),
//        scale = 0f,
//        rotationZ = 540f
//    )
//
//    // TODO Migrate to BaseMotionController
//
//    override fun PromoterModel.State<InteractionTarget>.toUiState(): List<MatchedUiState<InteractionTarget, MutableUiState>> {
//        TODO("Not yet implemented")
//    }
//
//    private fun <InteractionTarget : Any> PromoterModel.State<InteractionTarget>.toProps(): List<MatchedUiState<InteractionTarget, MutableUiState>> =
//        elements.map {
//            MatchedUiState(
//                it.first, when (it.second) {
//                    ElementState.CREATED -> created
//                    ElementState.STAGE1 -> stage1
//                    ElementState.STAGE2 -> stage2
//                    ElementState.STAGE3 -> stage3
//                    ElementState.STAGE4 -> stage4
//                    ElementState.STAGE5 -> stage5
//                    else -> destroyed
//                }
//            )
//        }
//
//    override fun mapSegment(
//        segment: Segment<PromoterModel.State<InteractionTarget>>,
//        segmentProgress: Flow<Float>,
//        initialProgress: Float
//    ): List<ElementUiModel<InteractionTarget>> {
//        val (fromState, targetState) = segment.stateTransition
//        val fromUiState = fromState.toUiState()
//        val targetUiState = targetState.toUiState()
//
//        return targetUiState.map { t1 ->
//            val t0 = fromUiState.find { it.element.id == t1.element.id }!!
//
//            ElementUiModel(
//                // TODO fix after migration to base interoplator
//                element = t1.element,
//                visibleState = MutableStateFlow(value = true),
//                animationContainer = {},
//                modifier = Modifier.composed {
//                    val segmentProgress by segmentProgress.collectAsState(initialProgress)
//                    val angleRadians0 = Math.toRadians(t0.targetUiState.angleDegrees.toDouble() - 90)
//                    val angleRadians1 = Math.toRadians(t1.targetUiState.angleDegrees.toDouble() - 90)
//
//                    // Lerp block
//                    val dpOffsetX =
//                        lerpFloat(
//                            t0.targetUiState.dpOffset.x.value,
//                            t1.targetUiState.dpOffset.x.value,
//                            segmentProgress
//                        )
//                    val dpOffsetY =
//                        lerpFloat(
//                            t0.targetUiState.dpOffset.y.value,
//                            t1.targetUiState.dpOffset.y.value,
//                            segmentProgress
//                        )
//                    val rotationY =
//                        lerpFloat(t0.targetUiState.rotationY, t1.targetUiState.rotationY, segmentProgress)
//                    val rotationZ =
//                        lerpFloat(t0.targetUiState.rotationZ, t1.targetUiState.rotationZ, segmentProgress)
//                    val scale = lerpFloat(t0.targetUiState.scale, t1.targetUiState.scale, segmentProgress)
//                    val angleRadians =
//                        lerpFloat(angleRadians0.toFloat(), angleRadians1.toFloat(), segmentProgress)
//                    val effectiveRadiusRatio = lerpFloat(
//                        t0.targetUiState.effectiveRadiusRatio,
//                        t1.targetUiState.effectiveRadiusRatio,
//                        segmentProgress
//                    )
//                    val effectiveRadius = radiusDp * effectiveRadiusRatio
//                    val x = (effectiveRadius * cos(angleRadians))
//                    val y = (effectiveRadius * sin(angleRadians))
//                    val arcOffsetDp = Offset(x, y)
//
//                    this.offset {
//                        IntOffset(
//                            x = (this.density * (halfWidthDp + dpOffsetX + arcOffsetDp.x)).roundToInt(),
//                            y = (this.density * (halfHeightDp + dpOffsetY + arcOffsetDp.y)).roundToInt()
//                        )
//                    }
//                        .graphicsLayer(
//                            rotationY = rotationY,
//                            rotationZ = rotationZ
//                        )
//                        .scale(scale)
//                },
//                progress = segmentProgress
//            )
//        }
//    }
//
//    override fun mapUpdate(
//        update: Update<PromoterModel.State<InteractionTarget>>
//    ): List<ElementUiModel<InteractionTarget>> {
//        TODO("Not yet implemented")
//    }
}
