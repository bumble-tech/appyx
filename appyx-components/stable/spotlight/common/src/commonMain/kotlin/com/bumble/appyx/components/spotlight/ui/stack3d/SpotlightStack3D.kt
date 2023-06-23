package com.bumble.appyx.components.spotlight.ui.stack3d

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.KeyframeSteps
import com.bumble.appyx.transitionmodel.ViewpointType
import com.bumble.appyx.transitionmodel.getFromTargetUiState
import com.bumble.appyx.transitionmodel.keyframeSteps
import com.bumble.appyx.transitionmodel.keyframeWith
import com.bumble.appyx.transitionmodel.mapTo
import kotlinx.coroutines.CoroutineScope

class SpotlightStack3D<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseMotionController<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
) {
    private val width: Dp = uiContext.transitionBounds.widthDp
    private val height: Dp = uiContext.transitionBounds.heightDp

    private val scrollY = GenericFloatProperty(uiContext, GenericFloatProperty.Target(0f))

    private val destroyed: TargetUiState = TargetUiState(
        effectiveIndex = GenericFloatProperty.Target(-1f),
        position = Position.Target(DpOffset(0.dp, height)),
        scale = Scale.Target(1f),
        alpha = Alpha.Target(0f),
        zIndex = ZIndex.Target(1f),
    )

    private fun topMost(): TargetUiState =
        TargetUiState(
            effectiveIndex = GenericFloatProperty.Target(0f),
            position = Position.Target(DpOffset.Zero),
            scale = Scale.Target(1f),
            alpha = Alpha.Target(1f),
            zIndex = ZIndex.Target(0f),
        )

    private fun stacked(fraction: Float, extraYOffset: Float = 0f, alpha: Float = 1f): TargetUiState =
        TargetUiState(
            effectiveIndex = GenericFloatProperty.Target(fraction),
            position = Position.Target(DpOffset(-0.12f * fraction * width, -(0.075f + extraYOffset) * fraction * height)),
            scale = Scale.Target(1f - 0.1f * fraction),
            alpha = Alpha.Target(alpha),
            zIndex = ZIndex.Target(-fraction),
        )

    private val yAxisKeyframes =
        keyframeSteps(
            -1.0f to destroyed,
            0.0f to topMost(),
            1.0f to stacked(1.0f),
            1.5f to stacked(1.5f, extraYOffset = 0.05f),
            2.0f to stacked(2.0f),
            3.0f to stacked(3.0f),
            4.0f to stacked(4.0f, alpha = 0f),
            effectiveIndexAccessor = { it.effectiveIndex.value },
        )

    override val viewpointDimensions: List<ViewpointType<State<InteractionTarget>, KeyframeSteps<TargetUiState>>> =
        listOf(
            { state: State<InteractionTarget> -> state.activeIndex } mapTo scrollY keyframeWith yAxisKeyframes
        )

    override fun State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        positions.flatMapIndexed { index, position ->
            position.elements.map {
                MatchedTargetUiState(
                    element = it.key,
                    targetUiState = yAxisKeyframes.getFromTargetUiState(activeIndex, index.toFloat())
                )
            }
        }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext)

    override suspend fun MutableUiState.mutableAnimateTo(scope: CoroutineScope, targetUiState: TargetUiState, springSpec: SpringSpec<Float>) =
        mutableAnimateTo(scope, targetUiState, springSpec, yAxisKeyframes)
}

