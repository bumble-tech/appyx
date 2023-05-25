package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Scale.Target
import com.bumble.appyx.interactions.core.ui.property.impl.Scale.Target.Scale.Companion.VectorConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Scale(
    uiContext: UiContext,
    target: Target,
    displacement: StateFlow<Target.Scale> = MutableStateFlow(Target.Scale(0.0f, 0.0f)),
    visibilityThreshold: Target.Scale = Target.Scale(scaleX = 0.01f, scaleY = 0.01f),
) : MotionProperty<Target.Scale, AnimationVector2D>(
    uiContext = uiContext,
    animatable = Animatable(target.value, VectorConverter),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<Target> {

    class Target(
        val value: Scale,
        val easing: Easing? = null,
    ) : MotionProperty.Target {

        class Scale(val scaleX: Float = 1.0f, val scaleY: Float = 1.0f) {

            operator fun minus(other: Scale): Scale =
                Scale(scaleX - other.scaleX, scaleY - other.scaleY)

            companion object {

                private val ScaleToVector: TwoWayConverter<Scale, AnimationVector2D> =
                    TwoWayConverter(
                        convertToVector = { AnimationVector2D(it.scaleX, it.scaleY) },
                        convertFromVector = { Scale(it.v1, it.v2) }
                    )

                val VectorConverter: TwoWayConverter<Scale, AnimationVector2D>
                    get() = ScaleToVector
            }
        }
    }

    override fun calculateRenderValue(
        base: Target.Scale,
        displacement: Target.Scale
    ): Target.Scale {
        return base - displacement
    }

    override val visibilityMapper: ((Target.Scale) -> Boolean) = { scale ->
        scale.scaleX > 0.0f && scale.scaleY > 0.0f
    }

    override val modifier: Modifier
        get() = Modifier.composed {
            val value = renderValueFlow.collectAsState().value
            this.scale(scaleX = value.scaleX, scaleY = value.scaleY)
        }

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {

        val scaleX = lerpFloat(
            start = start.value.scaleX,
            end = end.value.scaleX,
            progress = easingTransform(end.easing, fraction)
        )

        val scaleY = lerpFloat(
            start = start.value.scaleY,
            end = end.value.scaleY,
            progress = easingTransform(end.easing, fraction)
        )
        snapTo(Target.Scale(scaleX, scaleY))
    }
}
