package com.bumble.appyx.components.spotlight.ui.sliderrotation

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.state.BaseMutableUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MutableUiState(
    uiContext: UiContext,
    val position: Position,
    val scale: Scale,
    val rotationY: RotationY,
    val alpha: Alpha,
) : BaseMutableUiState<MutableUiState, TargetUiState>(
    uiContext = uiContext,
    motionProperties = listOf(scale, alpha, position)
) {
    override val modifier: Modifier
        get() = Modifier.then(position.modifier)
            .then(scale.modifier)
            .then(rotationY.modifier)
            .then(alpha.modifier)

    override suspend fun snapTo(scope: CoroutineScope, target: TargetUiState) {
        scope.launch {
            position.snapTo(target.position.value)
            scale.snapTo(target.scale.value)
            rotationY.snapTo(target.rotationY.value)
            alpha.snapTo(target.alpha.value)
        }
    }

    override suspend fun animateTo(
        scope: CoroutineScope,
        target: TargetUiState,
        springSpec: SpringSpec<Float>,
    ) {
        scope.launch {
            listOf(
                scope.async {
                    position.animateTo(
                        target.position.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                    scale.animateTo(
                        target.scale.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                    rotationY.animateTo(
                        target.rotationY.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                    alpha.animateTo(
                        target.alpha.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                }
            ).awaitAll()
        }
    }

    override fun lerpTo(scope: CoroutineScope, start: TargetUiState, end: TargetUiState, fraction: Float) {
        scope.launch {
            position.lerpTo(start.position, end.position, fraction)
            scale.lerpTo(start.scale, end.scale, fraction)
            rotationY.lerpTo(start.rotationY, end.rotationY, fraction)
            alpha.lerpTo(start.alpha, end.alpha, fraction)
        }
    }
}
