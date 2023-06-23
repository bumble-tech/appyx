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
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.lerpDpOffset
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Position.Target
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
), Interpolatable<Target> {

    class Target(
        val value: DpOffset,
        val easing: Easing? = null,
    ) : MotionProperty.Target<Target> {
        override fun lerpTo(end: Target, fraction: Float): Target =
            Target(
                value = lerpDpOffset(
                    start = value,
                    end = end.value,
                    progress = fraction,
                ),
                easing = end.easing,
            )
    }

    override fun calculateRenderValue(base: DpOffset, displacement: DpOffset): DpOffset =
        base - displacement

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
