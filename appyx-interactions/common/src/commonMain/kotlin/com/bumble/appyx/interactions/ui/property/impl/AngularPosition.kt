package com.bumble.appyx.interactions.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.ui.math.lerpFloat
import com.bumble.appyx.interactions.ui.property.Interpolatable
import com.bumble.appyx.interactions.ui.property.MotionProperty
import com.bumble.appyx.interactions.ui.property.impl.AngularPosition.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Suppress("MagicNumber")
class AngularPosition(
    coroutineScope: CoroutineScope,
    val target: Target,
    displacement: StateFlow<Value> = MutableStateFlow(Value.Zero),
    visibilityThreshold: Value = Value(0.1f, 0.1f)
) : MotionProperty<Value, AnimationVector2D>(
    coroutineScope = coroutineScope,
    animatable = Animatable(target.value, Value.VectorConverter),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<AngularPosition.Target> {

    class Value(
        val radius: Float,
        val angleDegrees: Float
    ) {
        val position: DpOffset
            get() {
                val angleRadians = (angleDegrees.toDouble() - 90) / 180 * PI

                return DpOffset(
                    x = (radius * cos(angleRadians)).dp,
                    y = (radius * sin(angleRadians)).dp
                )
            }

        companion object {
            val VectorConverter: TwoWayConverter<Value, AnimationVector2D> =
                TwoWayConverter(
                    convertToVector = { AnimationVector2D(
                        v1 = it.radius,
                        v2 = it.angleDegrees
                    )},
                    convertFromVector = {
                        Value(
                            radius = it.v1,
                            angleDegrees = it.v2
                        )
                    }
                )

            val Zero = Value(
                radius = 0f,
                angleDegrees = 0f
            )
        }
    }

    class Target(
        val value: Value,
        val easing: Easing? = null,
    ) : MotionProperty.Target

    override fun calculateRenderValue(base: Value, displacement: Value): Value =
        Value(
            base.radius - displacement.radius,
            base.angleDegrees - displacement.angleDegrees
        )

    override val modifier: Modifier
        get() = Modifier.composed {
            val value = renderValueFlow.collectAsState()
            this.offset(
                x = value.value.position.x,
                y = value.value.position.y
            )
        }

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {
        val progress = easingTransform(end.easing, fraction)
        snapTo(
            Value(
                radius = lerpFloat(start.value.radius, end.value.radius, progress),
                angleDegrees = lerpFloat(start.value.angleDegrees, end.value.angleDegrees, progress),
            )
        )
    }

}
