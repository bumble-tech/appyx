package com.bumble.appyx.interactions.ui.state

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.BaseMutableUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class TestMutableUiState(
    uiContext: UiContext,
    val positionAlignment: PositionAlignment,
) : BaseMutableUiState<TestTargetUiState>(
    uiContext = uiContext,
    motionProperties = listOf(positionAlignment),
) {
    override val combinedMotionPropertyModifier: Modifier = Modifier
        .then(positionAlignment.modifier)

    override suspend fun animateTo(
        scope: CoroutineScope,
        target: TestTargetUiState,
        springSpec: SpringSpec<Float>,
    ) {
        listOf(
            scope.async {
                positionAlignment.animateTo(
                    target.positionAlignment.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness),
                )
            },
        ).awaitAll()
    }

    override suspend fun snapTo(target: TestTargetUiState) {
        positionAlignment.snapTo(target.positionAlignment.value)
    }

    override fun lerpTo(
        scope: CoroutineScope,
        start: TestTargetUiState,
        end: TestTargetUiState,
        fraction: Float,
    ) {
        scope.launch {
            positionAlignment.lerpTo(start.positionAlignment, end.positionAlignment, fraction)
        }
    }
}
