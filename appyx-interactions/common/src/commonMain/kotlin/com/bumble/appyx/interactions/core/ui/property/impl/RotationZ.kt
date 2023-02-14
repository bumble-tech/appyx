package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable

class RotationZ(
    value: Float,
    visibilityThreshold: Float = 1f
) : AnimatedProperty<Float, AnimationVector1D>(
    animatable = Animatable(value, Float.VectorConverter),
    visibilityThreshold = visibilityThreshold
), Interpolatable<RotationZ> {

    override val modifier: Modifier
        get() = Modifier.composed {
            val value by animatable.asState()
            this.graphicsLayer {
                rotationZ = value
            }
        }

    override suspend fun lerpTo(start: RotationZ, end: RotationZ, fraction: Float) {
        snapTo(lerpFloat(start.value, end.value, fraction))
    }
}
