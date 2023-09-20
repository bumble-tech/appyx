package com.bumble.appyx.interactions.ui.state

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside
import com.bumble.appyx.interactions.core.ui.state.BaseMutableUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class TestMutableUiState(
    uiContext: UiContext,
    val position: PositionInside,
) : BaseMutableUiState<TestTargetUiState>(
    uiContext = uiContext,
    motionProperties = listOf(position),
) {
    override val combinedMotionPropertyModifier: Modifier = Modifier
        .then(position.modifier)

    override suspend fun animateTo(
        scope: CoroutineScope,
        target: TestTargetUiState,
        springSpec: SpringSpec<Float>,
    ) {
        listOf(
            scope.async {
                position.animateTo(
                    target.position.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness),
                )
            },
        ).awaitAll()
    }

    override suspend fun snapTo(target: TestTargetUiState) {
        position.snapTo(target.position.value)
    }

    override fun lerpTo(
        scope: CoroutineScope,
        start: TestTargetUiState,
        end: TestTargetUiState,
        fraction: Float,
    ) {
        scope.launch {
            position.lerpTo(start.position, end.position, fraction)
        }
    }
}
