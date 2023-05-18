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
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.lerpDpOffset
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.toPx
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Position(
    uiContext: UiContext,
    val target: Target,
    displacement: StateFlow<DpOffset> = MutableStateFlow(DpOffset.Zero),
    visibilityThreshold: DpOffset = DpOffset(1.dp, 1.dp),
) : MotionProperty<DpOffset, AnimationVector2D>(
    uiContext = uiContext,
    animatable = Animatable(target.value, DpOffset.VectorConverter),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<Position.Target> {

    class Target(
        val value: DpOffset,
        val easing: Easing? = null,
    ) : MotionProperty.Target

    override fun calculateRenderValue(base: DpOffset, displacement: DpOffset): DpOffset =
        base - displacement

    override val visibilityMapper: (DpOffset) -> Boolean = { displacedValue ->
        val bounds = uiContext.transitionBounds
        val clipToBounds = uiContext.clipToBounds

        val itemOffsetRangeX = calculateItemOffsetRangeX(displacedValue, bounds)
        val itemOffsetRangeY = calculateItemOffsetRangeY(displacedValue, bounds)

        val visibleOffsetRangeX = calculateVisibleOffsetRangeX(bounds, clipToBounds)
        val visibleOffsetRangeY = calculateVisibleOffsetRangeY(bounds, clipToBounds)

        val isVisible = itemOffsetRangeX.hasIntersection(visibleOffsetRangeX)
            && itemOffsetRangeY.hasIntersection(visibleOffsetRangeY)
        isVisible
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


    // If one range ends where another starts we consider them as non-intersected
    private fun <T : Comparable<T>> ClosedRange<T>.hasIntersection(another: ClosedRange<T>): Boolean =
        when {
            isEmpty() || another.isEmpty() -> false
            endInclusive <= another.start || start >= another.endInclusive -> false
            else -> true
        }

    override val modifier: Modifier
        get() = Modifier.composed {
            val value = renderValueFlow.collectAsState()
            this.offset(
                x = value.value.x,
                y = value.value.y
            )
        }

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {
        snapTo(
            lerpDpOffset(
                start = start.value,
                end = end.value,
                progress = easingTransform(end.easing, fraction)
            )
        )
    }

}
