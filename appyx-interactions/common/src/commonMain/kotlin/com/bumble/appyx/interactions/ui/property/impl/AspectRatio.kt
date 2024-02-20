package com.bumble.appyx.interactions.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.bumble.appyx.interactions.ui.math.lerpFloat
import com.bumble.appyx.interactions.ui.property.Interpolatable
import com.bumble.appyx.interactions.ui.property.MotionProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AspectRatio(
    coroutineScope: CoroutineScope,
    target: Target,
    visibilityThreshold: Float = 0.001f,
    displacement: StateFlow<Float> = MutableStateFlow(0f),
) : MotionProperty<Float, AnimationVector1D>(
    coroutineScope = coroutineScope,
    animatable = Animatable(target.value),
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<AspectRatio.Target> {

    class Target(
        val value: Float,
        val easing: Easing? = null,
    ) :  MotionProperty.Target

    override fun calculateRenderValue(base: Float, displacement: Float): Float =
        base - displacement

    override val modifier: Modifier
        get() = Modifier.composed {
            val value = renderValueFlow.collectAsState().value
            this.aspectRatio(value)
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
