package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpDpOffset
import com.bumble.appyx.interactions.core.ui.property.Interpolatable

class Offset(
    value: DpOffset,
    visibilityThreshold: DpOffset = DpOffset(1.dp, 1.dp)
) : AnimatedProperty<DpOffset, AnimationVector2D>(
    animatable = Animatable(value, DpOffset.VectorConverter),
    visibilityThreshold = visibilityThreshold
), Interpolatable<Offset> {

    var displacement: State<DpOffset> =
        mutableStateOf(DpOffset(0.dp, 0.dp))

    val displacedValue: State<DpOffset> =
        derivedStateOf {
            val animated = animatable.asState().value

            DpOffset(
                x = animated.x - displacement.value.x,
                y = animated.y - displacement.value.y
            )
        }

    override val modifier: Modifier
        get() = Modifier.composed {
            this.offset(
                x = displacedValue.value.x,
                y = displacedValue.value.y
            )
        }

    override suspend fun lerpTo(start: Offset, end: Offset, fraction: Float) {
        snapTo(lerpDpOffset(start.value, end.value, fraction))
    }

}
