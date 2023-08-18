package com.bumble.appyx.interactions.core.ui.property.impl.position

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
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
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

class PositionOutside(
    coroutineScope: CoroutineScope,
    val target: Target,
    displacement: StateFlow<Value> = MutableStateFlow(Value.Zero),
    visibilityThreshold: Value = Value(OutsideAlignment(0.01f, 0.01f), DpOffset(1.dp, 1.dp)),
) : MotionProperty<Value, AnimationVector4D>(
    coroutineScope = coroutineScope,
    animatable = Animatable(target.value, Value.VectorConverter),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<PositionOutside.Target> {

    data class Value(
        val alignment: OutsideAlignment = OutsideAlignment(0f, 0f),
        val offset: DpOffset = DpOffset.Zero
    ) {
        companion object {
            val VectorConverter: TwoWayConverter<Value, AnimationVector4D> =
                TwoWayConverter(
                    convertToVector = {
                        AnimationVector4D(
                            v1 = it.offset.x.value,
                            v2 = it.offset.y.value,
                            v3 = it.alignment.horizontalBias,
                            v4 = it.alignment.verticalBias,
                        )
                    },
                    convertFromVector = {
                        Value(
                            alignment = OutsideAlignment(it.v3, it.v4),
                            offset = DpOffset(it.v1.dp, it.v2.dp)
                        )
                    }
                )

            val Zero = Value(
                alignment = OutsideAlignment(0f, 0f),
                offset = DpOffset.Zero
            )
        }
    }

    class Target(
        val value: Value,
        val easing: Easing? = null,
    ) : MotionProperty.Target {

        constructor(
            offset: DpOffset
        ) : this(
            value = Value(
                alignment = OutsideAlignment(0f, 0f),
                offset = offset
            )
        )

        constructor(
            alignment: OutsideAlignment
        ) : this(
            value = Value(
                alignment = alignment,
                offset = DpOffset.Zero
            )
        )

        constructor(
            alignment: OutsideAlignment,
            offset: DpOffset,
        ) : this(
            value = Value(
                alignment = alignment,
                offset = offset
            )
        )
    }

    override fun calculateRenderValue(base: Value, displacement: Value): Value =
        Value(
            alignment = OutsideAlignment(
                horizontalBias = base.alignment.horizontalBias - displacement.alignment.horizontalBias,
                verticalBias = base.alignment.verticalBias - displacement.alignment.verticalBias,
            ),
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
                    .align(value.value.alignment)
            }

        }

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {
        val progress = easingTransform(end.easing, fraction)

        snapTo(
            Value(
                alignment = OutsideAlignment(
                    horizontalBias = lerpFloat(
                        start = start.value.alignment.horizontalBias,
                        end = end.value.alignment.horizontalBias,
                        progress = progress
                    ),
                    verticalBias = lerpFloat(
                        start = start.value.alignment.verticalBias,
                        end = end.value.alignment.verticalBias,
                        progress = progress
                    ),
                ),
                offset = lerpDpOffset(
                    start = start.value.offset,
                    end = end.value.offset,
                    progress = progress
                )
            )
        )
    }
}
