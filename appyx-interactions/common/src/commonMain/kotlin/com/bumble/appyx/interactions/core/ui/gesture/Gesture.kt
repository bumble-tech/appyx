package com.bumble.appyx.interactions.core.ui.gesture

import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.ui.math.proportionOf

open class Gesture<InteractionTarget, ModelState> internal constructor(
    val operation: Operation<ModelState>,
    val dragToProgress: (Offset) -> Float,
    val partial: (Offset, Float) -> Offset
) {
    var startProgress: Float? = null

    constructor(
        operation: Operation<ModelState>,
        completeAt: Offset,
    ) : this(
        operation = operation,
        dragToProgress = { offset -> proportionOf(offset, completeAt) },
        partial = { offset, remainder ->
            val p = proportionOf(offset, completeAt)
            val remp = remainder / p
            val ret = offset * remp
            ret
        }
    )

    class Noop<InteractionTarget, ModelState> : Gesture<InteractionTarget, ModelState>(
        operation = Operation.Noop(),
        dragToProgress = { 0f },
        partial = { _, _ -> Offset(0f, 0f) }
    )
}
