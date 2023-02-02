package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.DpOffset
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpDpOffset
import com.bumble.appyx.interactions.core.ui.property.Interpolatable

class Offset(
    value: DpOffset
) : AnimatedProperty<DpOffset, AnimationVector2D>(
    animatable = Animatable(value, DpOffset.VectorConverter)
), Interpolatable<Offset> {

    override val modifier: Modifier
        get() = Modifier.composed {
            val value = animatable.asState().value
            this.offset(x = value.x, y = value.y )
        }

    override suspend fun lerpTo(start: Offset, end: Offset, fraction: Float) {
        snapTo(lerpDpOffset(start.value, end.value, fraction))
    }

}
