package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.combineState
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.math.lerpDpOffset
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.toPx
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Position(
    val initialOffset: DpOffset,
    easing: Easing? = null,
    val clipToBounds: Boolean = false,
    val bounds: TransitionBounds? = null,
    val displacement: StateFlow<DpOffset> = MutableStateFlow(DpOffset.Zero),
    visibilityThreshold: DpOffset = DpOffset(1.dp, 1.dp),
) : MotionProperty<DpOffset, AnimationVector2D>(
    animatable = Animatable(initialOffset, DpOffset.VectorConverter),
    easing = easing,
    visibilityThreshold = visibilityThreshold,
), Interpolatable<Position> {

    override val visibilityMapper: (DpOffset) -> Boolean = { displacedValue ->
        val bounds = bounds
        if (bounds != null) {
            val itemOffsetRangeX = calculateItemOffsetRangeX(displacedValue, bounds)
            val itemOffsetRangeY = calculateItemOffsetRangeY(displacedValue, bounds)

            val visibleOffsetRangeX = calculateVisibleOffsetRangeX(bounds, clipToBounds)
            val visibleOffsetRangeY = calculateVisibleOffsetRangeY(bounds, clipToBounds)

            itemOffsetRangeX.hasIntersection(visibleOffsetRangeX)
                    && itemOffsetRangeY.hasIntersection(visibleOffsetRangeY)
        } else {
            true
        }
    }

    override val valueSource: StateFlow<DpOffset>
        get() = displacement.combineState(valueFlow, coroutineScope) { displacement, dpOffset ->
            DpOffset(
                x = dpOffset.x - displacement.x,
                y = dpOffset.y - displacement.y
            )
        }

    private fun calculateItemOffsetRangeX(
        displacedValue: DpOffset,
        bounds: TransitionBounds
    ): ClosedRange<Int> {
        val displacedValueXPx = displacedValue.x.toPx(bounds.density)
        return (displacedValueXPx..(displacedValueXPx + bounds.widthPx))
    }

    private fun calculateItemOffsetRangeY(
        displacedValue: DpOffset,
        bounds: TransitionBounds
    ): ClosedRange<Int> {
        val displacedValueYPx = displacedValue.y.toPx(bounds.density)
        return (displacedValueYPx..(displacedValueYPx + bounds.heightPx))
    }

    private fun calculateVisibleOffsetRangeX(bounds: TransitionBounds, clipToBounds: Boolean) =
        if (clipToBounds) {
            (0)..(bounds.widthPx)
        } else {
            (-bounds.containerOffsetXPx)..(bounds.screenWidthPx - bounds.containerOffsetXPx)
        }

    private fun calculateVisibleOffsetRangeY(bounds: TransitionBounds, clipToBounds: Boolean) =
        if (clipToBounds) {
            (0)..(bounds.heightPx)
        } else {
            (-bounds.containerOffsetYPx)..(bounds.screenHeightPx - bounds.containerOffsetYPx)
        }


    // if one range ends where another starts we consider them as non-intersected
    private fun <T : Comparable<T>> ClosedRange<T>.hasIntersection(another: ClosedRange<T>): Boolean =
        when {
            isEmpty() || another.isEmpty() -> false
            endInclusive <= another.start || start >= another.endInclusive -> false
            else -> true
        }

    override val modifier: Modifier
        get() = Modifier.composed {
            val displacedValue = valueSource.collectAsState()
            this.offset(
                x = displacedValue.value.x,
                y = displacedValue.value.y
            )
        }

    override suspend fun lerpTo(start: Position, end: Position, fraction: Float) {
        snapTo(
            lerpDpOffset(
                start = start.value,
                end = end.value,
                progress = easingTransform(end.easing, fraction)
            )
        )
    }

}
