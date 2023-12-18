package com.bumble.appyx.interactions.core.ui.property.impl.position

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.bumble.appyx.interactions.core.ui.LocalBoxScope
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.TopStart
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment.Companion.InContainer
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

@Suppress("MagicNumber")
class PositionAlignment(
    coroutineScope: CoroutineScope,
    val target: Target,
    displacement: StateFlow<Value> = MutableStateFlow(Value.Zero),
    visibilityThreshold: Value = Value(
        InsideAlignment(0.001f, 0.001f),
        OutsideAlignment(0.001f, 0.001f),
    ),
) : MotionProperty<Value, AnimationVector4D>(
    coroutineScope = coroutineScope,
    animatable = Animatable(target.value, Value.VectorConverter),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<PositionAlignment.Target> {

    data class Value(
        val insideAlignment: InsideAlignment = TopStart,
        val outsideAlignment: OutsideAlignment = InContainer,
    ) : Alignment {

        override fun align(
            size: IntSize,
            space: IntSize,
            layoutDirection: LayoutDirection
        ): IntOffset =
            insideAlignment.align(size, space, layoutDirection) +
                    outsideAlignment.align(size, space, layoutDirection)

        companion object {
            val VectorConverter: TwoWayConverter<Value, AnimationVector4D> =
                TwoWayConverter(
                    convertToVector = {
                        AnimationVector4D(
                            v1 = it.insideAlignment.horizontalBias,
                            v2 = it.insideAlignment.verticalBias,
                            v3 = it.outsideAlignment.horizontalBias,
                            v4 = it.outsideAlignment.verticalBias,
                        )
                    },
                    convertFromVector = {
                        Value(
                            insideAlignment = InsideAlignment(it.v1, it.v2),
                            outsideAlignment = OutsideAlignment(it.v3, it.v4),
                        )
                    }
                )

            val Zero = Value(
                insideAlignment = InsideAlignment(0f, 0f),
            )
        }
    }

    class Target(
        val value: Value,
        val easing: Easing? = null,
    ) : MotionProperty.Target {

        constructor() : this(
            value = Value(
                insideAlignment = TopStart,
                outsideAlignment = InContainer,
            )
        )

        constructor(
            outsideAlignment: OutsideAlignment
        ) : this(
            value = Value(
                insideAlignment = TopStart,
                outsideAlignment = outsideAlignment,
            )
        )

        constructor(
            insideAlignment: InsideAlignment
        ) : this(
            value = Value(
                insideAlignment = insideAlignment,
                outsideAlignment = InContainer,
            )
        )

        constructor(
            insideAlignment: InsideAlignment,
            outsideAlignment: OutsideAlignment
        ) : this(
            value = Value(
                insideAlignment = insideAlignment,
                outsideAlignment = outsideAlignment,
            )
        )
    }

    override fun calculateRenderValue(base: Value, displacement: Value): Value =
        Value(
            insideAlignment = InsideAlignment(
                horizontalBias = base.insideAlignment.horizontalBias - displacement.insideAlignment.horizontalBias,
                verticalBias = base.insideAlignment.verticalBias - displacement.insideAlignment.verticalBias,
            ),
            outsideAlignment = OutsideAlignment(
                horizontalBias = base.outsideAlignment.horizontalBias - displacement.outsideAlignment.horizontalBias,
                verticalBias = base.outsideAlignment.verticalBias - displacement.outsideAlignment.verticalBias,
            ),
        )

    override val modifier: Modifier
        get() = Modifier.composed {
            val boxScope = requireNotNull(LocalBoxScope.current)
            with(boxScope) {
                this@composed
                    .align(renderValue)
            }

        }

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {
        val progress = easingTransform(end.easing, fraction)

        snapTo(
            Value(
                insideAlignment = InsideAlignment(
                    horizontalBias = lerpFloat(
                        start = start.value.insideAlignment.horizontalBias,
                        end = end.value.insideAlignment.horizontalBias,
                        progress = progress
                    ),
                    verticalBias = lerpFloat(
                        start = start.value.insideAlignment.verticalBias,
                        end = end.value.insideAlignment.verticalBias,
                        progress = progress
                    ),
                ),
                outsideAlignment = OutsideAlignment(
                    horizontalBias = lerpFloat(
                        start = start.value.outsideAlignment.horizontalBias,
                        end = end.value.outsideAlignment.horizontalBias,
                        progress = progress
                    ),
                    verticalBias = lerpFloat(
                        start = start.value.outsideAlignment.verticalBias,
                        end = end.value.outsideAlignment.verticalBias,
                        progress = progress
                    ),
                ),
            )
        )
    }
}
