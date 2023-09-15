package com.bumble.appyx.interactions.core.ui.gesture

import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.ui.math.proportionOf

open class Gesture<InteractionTarget, ModelState> internal constructor(
    val operation: Operation<ModelState>,
    val isContinuous: Boolean,
    val dragToProgress: (Offset) -> Float,
    val partial: (Offset, Float) -> Offset
) {
    var startProgress: Float? = null

    /**
     * isContinuous: Boolean - indicates that if during a drag gesture this operation completes
     * but there's still offset to process a new gesture will be created that handles the remaining
     * amount.
     */
    constructor(
        operation: Operation<ModelState>,
        completeAt: Offset,
        isContinuous: Boolean = true,
    ) : this(
        operation = operation,
        isContinuous = isContinuous,
        dragToProgress = { offset -> proportionOf(offset, completeAt) },
        partial = { offset, remainder ->
            val totalProgress = proportionOf(offset, completeAt)
            offset * (remainder / totalProgress)
        }
    )

    class Noop<InteractionTarget, ModelState> : Gesture<InteractionTarget, ModelState>(
        operation = Operation.Noop(),
        isContinuous = false,
        dragToProgress = { 0f },
        partial = { _, _ -> Offset(0f, 0f) }
    )
}
