package com.bumble.appyx.interactions.ui.state

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bumble.appyx.combineState
import com.bumble.appyx.interactions.ui.context.TransitionBounds
import com.bumble.appyx.interactions.ui.context.TransitionBoundsAware
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.ui.property.MotionProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

abstract class BaseMutableUiState<TargetUiState>(
    val uiContext: UiContext,
    val motionProperties: List<MotionProperty<*, *>>
) : TransitionBoundsAware {

    private var containerRect = Rect(
        offset = Offset.Zero,
        size = Size(
            width = 0f,
            height = 0f
        )
    )

    val modifier
        get() = combinedMotionPropertyModifier
            .then(sizeChangedModifier)

    protected abstract val combinedMotionPropertyModifier: Modifier

    private val sizeChangedModifier: Modifier = Modifier
        .onSizeChanged { size ->
            this.size.update { size }
        }

    private val _isBoundsVisible = MutableStateFlow(false)
    private val visibilitySources: Iterable<StateFlow<Boolean>> =
        motionProperties.mapNotNull { it.isVisibleFlow } + _isBoundsVisible

    protected val size = MutableStateFlow(IntSize.Zero)

    val isVisible: StateFlow<Boolean> = combineState(
        visibilitySources,
        uiContext.coroutineScope
    ) { booleanArray ->
        booleanArray.all { it }
    }

    /**
     * This modifier duplicates original modifier, and will be placed on the dummy compose view with
     * the same size as original composable to calculate bounds relative to parent, and eventually update
     * bounds visibility relative to parent's bounds. Because it's responsible only for calculating
     * element's bounds it ensures that it's invisible by setting alpha as 0f.
     */
    val visibilityModifier: Modifier
        get() = Modifier
            .graphicsLayer {
                // Making sure that this modifier is invisible
                alpha = 0f
            }
            .then(combinedMotionPropertyModifier)
            .composed {
                val size by size.collectAsState()
                if (size != IntSize.Zero) {
                    val localDensity = LocalDensity.current.density
                    requiredSize(
                        DpSize(
                            (size.width / localDensity).dp,
                            (size.height / localDensity).dp
                        )
                    )
                } else {
                    fillMaxSize()
                }
            }
            .onGloballyPositioned { coordinates ->
                if (uiContext.clipToBounds) {
                    _isBoundsVisible.update {
                        coordinates.isVisibleInParent() && coordinates.isVisibleInWindow()
                    }
                } else {
                    _isBoundsVisible.update {
                        coordinates.isVisibleInWindow()
                    }
                }
            }

    override fun updateBounds(transitionBounds: TransitionBounds) {
        containerRect = Rect(
            offset = Offset.Zero,
            size = Size(
                width = transitionBounds.widthPx.toFloat(),
                height = transitionBounds.heightPx.toFloat()
            )
        )
    }

    private fun LayoutCoordinates.isVisibleInParent(): Boolean {
        val boundsInParent = this.boundsInParent()
        return boundsInParent.overlaps(containerRect)
    }

    // If element is invisible boundsInWindow will have width == 0 or height == 0 or both
    private fun LayoutCoordinates.isVisibleInWindow(): Boolean {
        val boundsInWindow = this.boundsInWindow()
        return boundsInWindow.width > 0f && boundsInWindow.height > 0f
    }

    @Suppress("unused")
    val isAnimating: Flow<Boolean> =
        combine(motionProperties.map { it.isAnimating }) { booleanArray ->
            booleanArray.any { it }
        }

    abstract suspend fun snapTo(
        target: TargetUiState
    )

    abstract fun lerpTo(
        scope: CoroutineScope,
        start: TargetUiState,
        end: TargetUiState,
        fraction: Float
    )

    @Suppress("unused")
    abstract suspend fun animateTo(
        scope: CoroutineScope,
        target: TargetUiState,
        springSpec: SpringSpec<Float>,
    )
}
