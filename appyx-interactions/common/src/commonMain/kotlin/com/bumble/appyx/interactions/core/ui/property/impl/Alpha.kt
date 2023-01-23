package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Property
import com.bumble.appyx.interactions.core.ui.property.Interpolatable

class Alpha(
    value: Float
) : Property<Float>, Interpolatable<Alpha> {

    private val animatable = Animatable(value)

    override val value: Float
        get() = animatable.value

    override val modifier: Modifier
        get() = Modifier.alpha(value)

    override suspend fun lerpTo(start: Alpha, end: Alpha, fraction: Float) {
        snapTo(lerpFloat(start.value, end.value, fraction))
    }

    override suspend fun snapTo(targetValue: Float) {
        animatable.snapTo(targetValue)
    }

    override suspend fun animateTo(targetValue: Float, animationSpec: AnimationSpec<Float>) {
        animatable.animateTo(targetValue, animationSpec)
    }
}
