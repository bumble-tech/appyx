package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import com.bumble.appyx.interactions.core.ui.math.lerpInt
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("MagicNumber")
class RoundedCorners(
    coroutineScope: CoroutineScope,
    target: Target,
    visibilityThreshold: Int = 1,
    displacement: StateFlow<Int> = MutableStateFlow(0),
) : MotionProperty<Int, AnimationVector1D>(
    coroutineScope = coroutineScope,
    animatable = Animatable(target.value, Int.VectorConverter),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<Target> {

    class Target(
        val value: Int,
        val easing: Easing? = null,
    ) : MotionProperty.Target

    override fun calculateRenderValue(base: Int, displacement: Int): Int =
        base - displacement

    override val modifier: Modifier
        get() = Modifier.composed {
            val value by renderValueFlow.collectAsState()
            this.clip(RoundedCornerShape(value.coerceIn(0, 100)))
        }

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {
        snapTo(
            lerpInt(
                start = start.value,
                end = end.value,
                progress = easingTransform(end.easing, fraction)
            )
        )
    }
}
