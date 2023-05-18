package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GenericFloatProperty(
    uiContext: UiContext,
    value: Float,
    displacement: StateFlow<Float> = MutableStateFlow(0f),
) : MotionProperty<Float, AnimationVector1D>(
    uiContext = uiContext,
    animatable = Animatable(value),
    displacement = displacement
), Interpolatable<GenericFloatProperty> {

    override fun calculateRenderValue(base: Float, displacement: Float): Float =
        base - displacement

    override val modifier: Modifier
        get() = Modifier

    override suspend fun lerpTo(start: GenericFloatProperty, end: GenericFloatProperty, fraction: Float) {
        snapTo(
            lerpFloat(
                start = start.internalValue,
                end = end.internalValue,
                progress = easingTransform(end.easing, fraction)
            )
        )
    }
}
