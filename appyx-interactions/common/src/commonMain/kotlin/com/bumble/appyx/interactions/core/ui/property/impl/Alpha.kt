package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Easing
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.interactions.core.ui.helper.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty

class Alpha(
    value: Float,
    easing: Easing? = null,
    visibilityThreshold: Float = 0.01f
) : MotionProperty<Float, AnimationVector1D>(
    animatable = Animatable(value),
    easing = easing,
    visibilityThreshold = visibilityThreshold
), Interpolatable<Alpha> {

    override val modifier: Modifier
        get() = Modifier.composed {
            this.alpha(animatable.asState().value)
        }

    override suspend fun lerpTo(start: Alpha, end: Alpha, fraction: Float) {
        snapTo(
            lerpFloat(
                start = start.value,
                end = end.value,
                progress = easingTransform(end.easing, fraction)
            )
        )
    }
}
