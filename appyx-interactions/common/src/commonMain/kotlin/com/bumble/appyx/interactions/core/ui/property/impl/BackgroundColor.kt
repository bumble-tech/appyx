package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import androidx.compose.ui.graphics.lerp as lerpColor

class BackgroundColor(
    value: Color,
    visibilityThreshold: Color = Color(1, 1, 1, 1)
) : AnimatedProperty<Color, AnimationVector4D>(
    animatable = Animatable(value, Color.VectorConverter(value.colorSpace)),
    visibilityThreshold = visibilityThreshold
), Interpolatable<BackgroundColor> {

    override val modifier: Modifier
        get() = Modifier.composed {
            this.background(color = animatable.asState().value)
        }

    override suspend fun lerpTo(start: BackgroundColor, end: BackgroundColor, fraction: Float) {
        snapTo(lerpColor(start.value, end.value, fraction))
    }

}
