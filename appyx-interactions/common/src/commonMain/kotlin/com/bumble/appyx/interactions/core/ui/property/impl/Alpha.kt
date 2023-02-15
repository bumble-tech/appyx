package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable

class Alpha(
    value: Float
) : AnimatedProperty<Float, AnimationVector1D>(
    animatable = Animatable(value)
), Interpolatable<Alpha> {

    override val isVisible: Boolean
        get() = value > 0f

    override val modifier: Modifier
        get() = Modifier.composed {
            this.alpha(animatable.asState().value)
        }

    override suspend fun lerpTo(start: Alpha, end: Alpha, fraction: Float) {
        snapTo(lerpFloat(start.value, end.value, fraction))
    }
}
