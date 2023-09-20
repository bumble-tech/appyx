package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.RotationX.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RotationX(
    coroutineScope: CoroutineScope,
    target: Target,
    visibilityThreshold: Float = 1f,
    displacement: StateFlow<Float> = MutableStateFlow(0f),
    private val origin: TransformOrigin = target.origin,
) : MotionProperty<Float, AnimationVector1D>(
    coroutineScope = coroutineScope,
    animatable = Animatable(target.value, Float.VectorConverter),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<Target> {

    class Target(
        val value: Float,
        val origin: TransformOrigin = TransformOrigin.Center,
        val easing: Easing? = null,
    ) : MotionProperty.Target

    override fun calculateRenderValue(base: Float, displacement: Float): Float =
        base - displacement

    override val modifier: Modifier
        get() = Modifier.composed {
            val value by renderValueFlow.collectAsState()
            this.graphicsLayer {
                rotationX = value
                transformOrigin = origin
            }
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
