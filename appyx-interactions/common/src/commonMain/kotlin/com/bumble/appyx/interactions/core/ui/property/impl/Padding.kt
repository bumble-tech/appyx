package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Padding(
    uiContext: UiContext,
    target: Target,
    displacement: StateFlow<Target.PaddingValue> = MutableStateFlow(Target.PaddingValue()),
    visibilityThreshold: Target.PaddingValue = Target.PaddingValue(1.dp, 1.dp, 1.dp, 1.dp),
) : MotionProperty<Padding.Target.PaddingValue, AnimationVector4D>(
    uiContext = uiContext,
    animatable = Animatable(target.value, Target.PaddingValue.VectorConverter),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<Padding.Target> {

    class Target(
        val value: PaddingValue = PaddingValue(),
        val easing: Easing? = null,
    ) : MotionProperty.Target {


        class PaddingValue(
            val top: Dp = 0.dp,
            val bottom: Dp = 0.dp,
            val start: Dp = 0.dp,
            val end: Dp = 0.dp
        ) {

            companion object {
                val VectorConverter: TwoWayConverter<PaddingValue, AnimationVector4D> =
                    TwoWayConverter(
                        convertToVector = {
                            AnimationVector4D(
                                it.top.value,
                                it.bottom.value,
                                it.start.value,
                                it.end.value
                            )
                        },
                        convertFromVector = { PaddingValue(it.v1.dp, it.v2.dp, it.v3.dp, it.v4.dp) }
                    )
            }
        }
    }

    override val modifier: Modifier
        get() = Modifier.composed {
            val value = renderValueFlow.collectAsState().value
            this.padding(
                top = value.top,
                bottom = value.bottom,
                start = value.start,
                end = value.end
            )
        }

    override fun calculateRenderValue(
        base: Target.PaddingValue,
        displacement: Target.PaddingValue
    ) = Target.PaddingValue(
        top = base.top + displacement.top,
        bottom = base.bottom + displacement.bottom,
        start = base.start + displacement.start,
        end = base.end + displacement.end
    )

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {
        val paddingTop = lerp(
            start = start.value.top,
            stop = end.value.top,
            fraction = easingTransform(end.easing, fraction)
        )

        val paddingBottom = lerp(
            start = start.value.bottom,
            stop = end.value.bottom,
            fraction = easingTransform(end.easing, fraction)
        )
        val paddingStart = lerp(
            start = start.value.start,
            stop = end.value.start,
            fraction = easingTransform(end.easing, fraction)
        )

        val paddingEnd = lerp(
            start = start.value.end,
            stop = end.value.end,
            fraction = easingTransform(end.easing, fraction)
        )
        snapTo(
            Target.PaddingValue(paddingTop, paddingBottom, paddingStart, paddingEnd)
        )
    }
}
