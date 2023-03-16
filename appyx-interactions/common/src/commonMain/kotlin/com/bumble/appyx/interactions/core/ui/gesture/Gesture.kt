package com.bumble.appyx.interactions.core.ui.gesture

import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.interactions.core.model.transition.Operation

open class Gesture<InteractionTarget, ModelState>(
    val operation: Operation<ModelState>,
    val dragToProgress: (Offset) -> Float,
    val partial: (Offset, Float) -> Offset
) {
    var startProgress: Float? = null

    class Noop<InteractionTarget, ModelState> : Gesture<InteractionTarget, ModelState>(
        operation = Operation.Noop(),
        dragToProgress = { 0f },
        partial = { _, _ -> Offset(0f, 0f) }
    )
}
