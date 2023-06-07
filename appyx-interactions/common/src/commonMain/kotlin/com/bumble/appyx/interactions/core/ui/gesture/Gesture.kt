package com.bumble.appyx.interactions.core.ui.gesture

import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.ui.math.proportionOf

open class Gesture<InteractionTarget, ModelState>(
    val operation: Operation<ModelState>,
    val dragToProgress: (Offset) -> Float,
    val partial: (Offset, Float) -> Offset
) {
    var startProgress: Float? = null

    constructor(
        operation: Operation<ModelState>,
        axis: Offset,
    ) : this(
        operation = operation,
        dragToProgress = { offset -> proportionOf(offset, axis) },
        partial = { offset, remainder ->
            val p = proportionOf(offset, axis)
            val aligned = axis * p
            val spent = aligned * (1 - remainder)
            val ret = offset - spent
            ret
        }
    )

    class Noop<InteractionTarget, ModelState> : Gesture<InteractionTarget, ModelState>(
        operation = Operation.Noop(),
        dragToProgress = { 0f },
        partial = { _, _ -> Offset(0f, 0f) }
    )
}
