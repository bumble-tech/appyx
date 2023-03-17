package com.bumble.appyx.interactions.core.ui.state

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.Modifier
import com.bumble.appyx.combineState
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

abstract class BaseUiState<T>(
    val motionProperties: List<MotionProperty<*, *>>,
    val coroutineScope: CoroutineScope
) {
    abstract val modifier: Modifier

    val isAnimating: Flow<Boolean>
        get() = combine(motionProperties.map { it.isAnimating }) { booleanArray ->
            booleanArray.any { it }
        }

    val isVisible: StateFlow<Boolean>
        get() = combineState(
            motionProperties.map { it.isVisibleFlow },
            coroutineScope
        ) { booleanArray ->
            booleanArray.all { it }
        }

    abstract suspend fun snapTo(
        scope: CoroutineScope,
        uiState: T
    )

    abstract fun lerpTo(
        scope: CoroutineScope,
        start: T,
        end: T,
        fraction: Float
    )

    abstract suspend fun animateTo(
        scope: CoroutineScope,
        uiState: T,
        springSpec: SpringSpec<Float>,
    )
}
