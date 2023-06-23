package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.background
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor.Target
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.graphics.lerp as lerpColor

class BackgroundColor(
    uiContext: UiContext,
    target: Target,
    displacement: StateFlow<Color> = MutableStateFlow(Color.Unspecified),
    visibilityThreshold: Color = Color(1, 1, 1, 1),
) : MotionProperty<Color, AnimationVector4D>(
    uiContext = uiContext,
    animatable = Animatable(target.value, Color.VectorConverter(target.value.colorSpace)),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<Target> {

    class Target(
        val value: Color,
        val easing: Easing? = null,
    ) : MotionProperty.Target<Target> {
        override fun lerpTo(end: Target, fraction: Float): Target =
            Target(
                value = lerpColor(
                    start = value,
                    stop = end.value,
                    fraction = fraction,
                ),
                easing = end.easing,
            )
    }

    /**
     * This class currently doesn't support color displacement.
     * Another MotionProperty class based on a non-compose Color class that has
     * more functionality (e.g. ajalt/colormath) can implement HSV or other color displacements
     * as required.
     */
    override fun calculateRenderValue(base: Color, displacement: Color): Color =
        base

    override val modifier: Modifier
        get() = Modifier.composed {
            this.background(color = renderValueFlow.collectAsState().value)
        }

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {
        snapTo(
            lerpColor(
                start = start.value,
                stop = end.value,
                fraction = easingTransform(end.easing, fraction)
            )
        )
    }
}
