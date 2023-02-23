package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.interactions.core.Comparable
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import androidx.compose.ui.graphics.lerp as lerpColor

class BackgroundColor(
    value: Color,
    easing: Easing? = null,
    visibilityThreshold: Color = Color(1, 1, 1, 1)
) : AnimatedProperty<Color, AnimationVector4D>(
    animatable = Animatable(value, Color.VectorConverter(value.colorSpace)),
    easing = easing,
    visibilityThreshold = visibilityThreshold
), Interpolatable<BackgroundColor>, Comparable<BackgroundColor> {

    override val modifier: Modifier
        get() = Modifier.composed {
            this.background(color = animatable.asState().value)
        }

    override fun isEqualTo(other: BackgroundColor) =
        value.component1() == other.value.component1() &&
                value.component2() == other.value.component2() &&
                value.component3() == other.value.component3() &&
                value.component4() == other.value.component4() &&
                value.component5() == other.value.component5()

    override suspend fun lerpTo(start: BackgroundColor, end: BackgroundColor, fraction: Float) {
        snapTo(
            lerpColor(
                start = start.value,
                stop = end.value,
                fraction = easingTransform(end.easing, fraction)
            )
        )
    }


}
