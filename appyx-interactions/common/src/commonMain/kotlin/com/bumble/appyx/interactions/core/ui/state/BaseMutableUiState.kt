package com.bumble.appyx.interactions.core.ui.state

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.Modifier
import com.bumble.appyx.combineState
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

abstract class BaseMutableUiState<MutableUiState, TargetUiState>(
    val uiContext: UiContext,
    val motionProperties: List<MotionProperty<*, *>>
) {
    abstract val modifier: Modifier

    val isAnimating: Flow<Boolean>
        get() = combine(motionProperties.map { it.isAnimating }) { booleanArray ->
            booleanArray.any { it }
        }

    val isVisible: StateFlow<Boolean>
        get() = combineState(
            motionProperties.map { it.isVisibleFlow },
            uiContext.coroutineScope
        ) { booleanArray ->
            booleanArray.all { it }
        }

    abstract suspend fun snapTo(
        scope: CoroutineScope,
        target: TargetUiState
    )

    abstract fun lerpTo(
        scope: CoroutineScope,
        start: TargetUiState,
        end: TargetUiState,
        fraction: Float
    )

    abstract suspend fun animateTo(
        scope: CoroutineScope,
        target: TargetUiState,
        springSpec: SpringSpec<Float>,
    )
}
