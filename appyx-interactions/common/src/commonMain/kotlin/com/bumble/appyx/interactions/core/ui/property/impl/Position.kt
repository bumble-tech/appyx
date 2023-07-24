package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.lerpDpOffset
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Position.Value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Position(
    uiContext: UiContext,
    val target: Target,
    displacement: StateFlow<Value> = MutableStateFlow(Value.Zero),
    visibilityThreshold: Value = Value(BiasAlignment(0.01f, 0.01f), DpOffset(1.dp, 1.dp)),
) : MotionProperty<Value, AnimationVector4D>(
    uiContext = uiContext,
    animatable = Animatable(target.value, Value.VectorConverter),
    easing = target.easing,
    visibilityThreshold = visibilityThreshold,
    displacement = displacement
), Interpolatable<Position.Target> {

    object Alignment {

        @Stable
        val TopStart = BiasAlignment(-1f, -1f)
        @Stable
        val TopCenter = BiasAlignment(0f, -1f)
        @Stable
        val TopEnd = BiasAlignment(1f, -1f)
        @Stable
        val CenterStart = BiasAlignment(-1f, 0f)
        @Stable
        val Center = BiasAlignment(0f, 0f)
        @Stable
        val CenterEnd = BiasAlignment(1f, 0f)
        @Stable
        val BottomStart = BiasAlignment(-1f, 1f)
        @Stable
        val BottomCenter = BiasAlignment(0f, 1f)
        @Stable
        val BottomEnd = BiasAlignment(1f, 1f)
    }

    data class Value(
        val alignment: BiasAlignment = DefaultAlignment,
        val offset: DpOffset
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
                            alignment = BiasAlignment(it.v3, it.v4),
                            offset = DpOffset(it.v1.dp, it.v2.dp)
                        )
                    }
                )

            val Zero = Value(
                alignment = DefaultAlignment,
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
                alignment = DefaultAlignment,
                offset = offset
            )
        )

        constructor(
            alignment: BiasAlignment
        ) : this(
            value = Value(
                alignment = alignment,
                offset = DpOffset.Zero
            )
        )

        constructor(
            alignment: BiasAlignment,
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
            alignment = BiasAlignment(
                horizontalBias = base.alignment.horizontalBias - displacement.alignment.horizontalBias,
                verticalBias = base.alignment.verticalBias - displacement.alignment.verticalBias,
            ),
            offset = base.offset - displacement.offset
        )

    override val modifier: Modifier
        get() = Modifier.composed {
            val value = renderValueFlow.collectAsState()
            val boxScope: BoxScope = uiContext.boxScope

            with(boxScope) {
                this@composed
                    .offset(
                        x = value.value.offset.x,
                        y = value.value.offset.y
                    )
                    .align(value.value.alignment)
            }

        }

    override suspend fun lerpTo(start: Target, end: Target, fraction: Float) {
        val progress = easingTransform(end.easing, fraction)

        snapTo(
            Value(
                alignment = BiasAlignment(
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

// The effect of using this as a default value is TopStart as intended,
// but I found I needed to use the values that reflect Center (0,0),
// and I have no idea why!
private val DefaultAlignment: BiasAlignment = BiasAlignment(0f, 0f)

