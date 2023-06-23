package com.bumble.appyx.components.spotlight.ui.stack3d

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.transitionmodel.KeyframeSteps
import com.bumble.appyx.transitionmodel.lerpTargetUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update

// All functions from this file is code that should be auto-generated using KSP

suspend fun MutableUiState.mutableAnimateTo(
    scope: CoroutineScope,
    targetUiState: TargetUiState,
    springSpec: SpringSpec<Float>,
    keyframeSteps: KeyframeSteps<TargetUiState>
) {
    val motionPropertiesFlow = MutableStateFlow(arrayListOf<MotionProperty.Target<*>>())
    listOf(
        scope.async {
            effectiveIndex.animateTo(
                targetUiState.effectiveIndex.value,
                spring(springSpec.dampingRatio, springSpec.stiffness),
            ) {
                val newTarget = keyframeSteps.lerpTargetUiState(this.value, ::lerp)
                motionPropertiesFlow.update {
                    arrayListOf(
                        newTarget.position,
                        newTarget.scale,
                        newTarget.alpha,
                        newTarget.zIndex,
                    )
                }
            }
        },
        scope.async {
            motionPropertiesFlow.collect {
                if (it.isNotEmpty()) {
                    position.snapTo((it[0] as Position.Target).value)
                    scale.snapTo((it[1] as Scale.Target).value)
                    alpha.snapTo((it[2] as Alpha.Target).value)
                    zIndex.snapTo((it[3] as ZIndex.Target).value)
                }
            }
        }
    ).awaitAll()
}

fun lerp(from: TargetUiState, to: TargetUiState, fraction: Float): TargetUiState =
    TargetUiState(
        effectiveIndex = from.effectiveIndex.lerpTo(to.effectiveIndex, fraction),
        position = from.position.lerpTo(to.position, fraction),
        scale = from.scale.lerpTo(to.scale, fraction),
        alpha = from.alpha.lerpTo(to.alpha, fraction),
        zIndex = from.zIndex.lerpTo(to.zIndex, fraction),
    )

