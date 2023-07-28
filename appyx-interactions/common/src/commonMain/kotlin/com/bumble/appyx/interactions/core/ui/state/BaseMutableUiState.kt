package com.bumble.appyx.interactions.core.ui.state

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.bumble.appyx.combineState
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

abstract class BaseMutableUiState<TargetUiState>(
    val uiContext: UiContext,
    val motionProperties: List<MotionProperty<*, *>>
) {

    private val containerRect = Rect(
        offset = Offset.Zero,
        size = Size(
            width = uiContext.transitionBounds.widthPx.toFloat(),
            height = uiContext.transitionBounds.heightPx.toFloat()
        )
    )

    abstract val modifier: Modifier

    private val _isBoundsVisible = MutableStateFlow(false)
    private val visibilitySources: Iterable<StateFlow<Boolean>> =
        motionProperties.mapNotNull { it.isVisibleFlow } + _isBoundsVisible

    @Suppress("unused")
    val isVisible: StateFlow<Boolean> = combineState(
        visibilitySources,
        uiContext.coroutineScope
    ) { booleanArray ->
        booleanArray.all { it }
    }

    /**
     * This modifier duplicates original modifier, and will be placed on the dummy compose view
     * to calculate bounds relative to parent and eventually update bounds visibility relative
     * to parent's bounds. Because it's responsible only for calculating element's bounds it ensures
     * that it's invisible by setting alpha as 0f. Additionally, it makes sure that it occupies all
     * available space by applying fillMaxSize().
     */
    @Suppress("unused")
    val visibilityModifier: Modifier
        get() = Modifier
            .graphicsLayer {
                // Making sure that this modifier is invisible
                alpha = 0f
            }
            .then(modifier)
            .fillMaxSize()
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
