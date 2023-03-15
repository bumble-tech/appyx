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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

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
            // TODO calculate for y axis as well
            val displacedValueX = displacedValue.x.value
            val leftEdgeOffsetDp = displacedValueX.roundToInt()
            val rightEdgeOffsetDp = leftEdgeOffsetDp + bounds.widthDp.value.roundToInt()
            val visibleWindowLeftEdge = calculatedWindowLeftEdge(bounds)
            val visibleWindowRightEdge = calculateWindowRightEdge(bounds)
            (rightEdgeOffsetDp <= visibleWindowLeftEdge || leftEdgeOffsetDp >= visibleWindowRightEdge).not()
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

    private fun calculateWindowRightEdge(bounds: TransitionBounds) = if (clipToBounds) {
        bounds.widthDp.value.roundToInt()
    } else {
        (bounds.screenWidthDp - bounds.containerOffsetX).value.roundToInt()
    }

    private fun calculatedWindowLeftEdge(bounds: TransitionBounds): Int {
        return if (clipToBounds) {
            0
        } else {
            -bounds.containerOffsetX.value.roundToInt()
        }
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
