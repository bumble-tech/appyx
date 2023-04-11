package com.bumble.appyx.transitionmodel.spotlight.ui.slider

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
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
    val alpha: Alpha,
//        val zIndex: Float = 1f,               FIXME
//        val aspectRatio: Float = 0.42f,       FIXME
//        val rotation: Float = 0f,             FIXME
) : BaseMutableUiState<MutableUiState, TargetUiState>(
    uiContext = uiContext,
    motionProperties = listOf(scale, alpha, position)
) {
    override val modifier: Modifier
        get() = Modifier.then(position.modifier)
            .then(alpha.modifier)
            .then(scale.modifier)

    override suspend fun snapTo(scope: CoroutineScope, target: TargetUiState) {
        scope.launch {
            position.snapTo(target.position.value)
            alpha.snapTo(target.alpha.value)
            scale.snapTo(target.scale.value)
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
                    alpha.animateTo(
                        target.alpha.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                    scale.animateTo(
                        target.scale.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                }
            ).awaitAll()
        }
    }

    override fun lerpTo(scope: CoroutineScope, start: TargetUiState, end: TargetUiState, fraction: Float) {
        scope.launch {
            position.lerpTo(start.position, end.position, fraction)
            alpha.lerpTo(start.alpha, end.alpha, fraction)
            scale.lerpTo(start.scale, end.scale, fraction)
        }
    }
}
