package com.bumble.appyx.interactions.core.progress

import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.interactions.core.Operation

open class Gesture<NavTarget, ModelState>(
    val operation: Operation<ModelState>,
    val dragToProgress: (Offset) -> Float,
    val partial: (Offset, Float) -> Offset
) {
    var startProgress: Float? = null

    class Noop<NavTarget, ModelState> : Gesture<NavTarget, ModelState>(
        operation = Operation.Noop(),
        dragToProgress = { 0f },
        partial = { _, _ -> Offset(0f, 0f) }
    )
}
