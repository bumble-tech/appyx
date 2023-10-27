package com.bumble.appyx.interactions.core.ui.property.impl.position

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.LocalBoxScope
import com.bumble.appyx.interactions.core.ui.math.lerpDpOffset
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOffset.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

@Suppress("MagicNumber")
class PositionOffset(
    coroutineScope: CoroutineScope,
    val target: Target,
    displacement: StateFlow<Value> = MutableStateFlow(Value.Zero),
    visibilityThreshold: Value = Value(DpOffset(1.dp, 1.dp)),
) : MotionProperty<Value, AnimationVector2D>(
    coroutineScope = coroutineScope,
    animatable = Animatable(target.value, Value.VectorConverter),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<PositionOffset.Target> {

    data class Value(
        val offset: DpOffset
    ) {
        companion object {
            val VectorConverter: TwoWayConverter<Value, AnimationVector2D> =
                TwoWayConverter(
                    convertToVector = {
                        AnimationVector2D(
                            v1 = it.offset.x.value,
                            v2 = it.offset.y.value,
                        )
                    },
                    convertFromVector = {
                        Value(
                            offset = DpOffset(it.v1.dp, it.v2.dp)
                        )
                    }
                )

            val Zero = Value(
                offset = DpOffset.Zero
            )
        }
    }

    class Target(
        val value: Value,
        val easing: Easing? = null,
    ) : MotionProperty.Target {

        constructor(
            offset: DpOffset = DpOffset.Zero
        ) : this(
            value = Value(
                offset = offset
            )
        )
    }

    override fun calculateRenderValue(base: Value, displacement: Value): Value =
        Value(
            offset = base.offset - displacement.offset
        )

    override val modifier: Modifier
        get() = Modifier.composed {
            val value = renderValueFlow.collectAsState()
            val boxScope = requireNotNull(LocalBoxScope.current)
            with(boxScope) {
                this@composed
                    .offset {
                        IntOffset(
                            x = (value.value.offset.x.value * density).roundToInt(),
                            y = (value.value.offset.y.value * density).roundToInt(),
                        )
                    }
            }

        }

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {
        val progress = easingTransform(end.easing, fraction)

        snapTo(
            Value(
                offset = lerpDpOffset(
                    start = start.value.offset,
                    end = end.value.offset,
                    progress = progress
                )
            )
        )
    }
}
