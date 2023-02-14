package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable

class Scale(
    value: Float,
    visibilityThreshold: Float = 0.01f
) : AnimatedProperty<Float, AnimationVector1D>(
    animatable = Animatable(value, Float.VectorConverter),
    visibilityThreshold = visibilityThreshold
), Interpolatable<Scale> {

    override val modifier: Modifier
        get() = Modifier.composed {
            val value by animatable.asState()
            this.scale(value)
        }

    override suspend fun lerpTo(start: Scale, end: Scale, fraction: Float) {
        snapTo(lerpFloat(start.value, end.value, fraction))
    }
}
