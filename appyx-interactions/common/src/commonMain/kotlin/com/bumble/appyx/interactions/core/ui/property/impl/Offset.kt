package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.helper.lerpDpOffset
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.MotionProperty

class Offset(
    value: DpOffset,
    easing: Easing? = null,
    visibilityThreshold: DpOffset = DpOffset(1.dp, 1.dp)
) : MotionProperty<DpOffset, AnimationVector2D>(
    animatable = Animatable(value, DpOffset.VectorConverter),
    easing = easing,
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
        snapTo(
            lerpDpOffset(
                start = start.value,
                end = end.value,
                progress = easingTransform(end.easing, fraction)
            )
        )
    }

}
