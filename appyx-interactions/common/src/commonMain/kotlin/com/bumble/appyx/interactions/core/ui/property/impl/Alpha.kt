package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Easing
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty

class Alpha(
    uiContext: UiContext,
    target: Target,
    visibilityThreshold: Float = 0.01f
) : MotionProperty<Float, AnimationVector1D>(
    uiContext = uiContext,
    animatable = Animatable(target.value),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold
), Interpolatable<Alpha.Target> {

    class Target(
        val value: Float,
        val easing: Easing? = null,
    ) : MotionProperty.Target

    override val visibilityMapper: ((Float) -> Boolean) = { alpha ->
        alpha > 0.0f
    }

    override val modifier: Modifier
        get() = Modifier.composed {
            this.alpha(animatable.asState().value)
        }

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {
        snapTo(
            lerpFloat(
                start = start.value,
                end = end.value,
                progress = easingTransform(end.easing, fraction)
            )
        )
    }
}
