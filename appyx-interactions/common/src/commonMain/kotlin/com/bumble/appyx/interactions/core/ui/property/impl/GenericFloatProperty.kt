package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty

class GenericFloatProperty(
    uiContext: UiContext,
    value: Float
) : MotionProperty<Float, AnimationVector1D>(
    uiContext = uiContext,
    animatable = Animatable(value)
), Interpolatable<GenericFloatProperty> {

    override val modifier: Modifier
        get() = Modifier

    override suspend fun lerpTo(start: GenericFloatProperty, end: GenericFloatProperty, fraction: Float) {
        snapTo(
            lerpFloat(
                start = start.value,
                end = end.value,
                progress = easingTransform(end.easing, fraction)
            )
        )
    }
}
