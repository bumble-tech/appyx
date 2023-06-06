package com.bumble.appyx.components.spotlight.ui.fader

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.state.BaseMutableUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MutableUiState(
    uiContext: UiContext,
    val alpha: Alpha,
) : BaseMutableUiState<MutableUiState, TargetUiState>(
    uiContext = uiContext,
    motionProperties = listOf(alpha)
) {
    override val modifier: Modifier
        get() = alpha.modifier

    override suspend fun snapTo(target: TargetUiState) {
        alpha.snapTo(target.alpha.value)
    }

    override suspend fun animateTo(
        scope: CoroutineScope,
        target: TargetUiState,
        springSpec: SpringSpec<Float>,
    ) {
        scope.launch {
            listOf(
                scope.async {
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
            alpha.lerpTo(start.alpha, end.alpha, fraction)
        }
    }
}
