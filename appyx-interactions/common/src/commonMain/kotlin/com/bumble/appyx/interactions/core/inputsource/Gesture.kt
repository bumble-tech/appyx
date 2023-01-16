package com.bumble.appyx.interactions.core.inputsource

import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.interactions.core.Operation

open class Gesture<NavTarget, State>(
    val operation: Operation<NavTarget, State>,
    val dragToProgress: (Offset) -> Float,
    val partial: (Offset, Float) -> Offset
) {
    var startProgress: Float? = null

    class Noop<NavTarget, State> : Gesture<NavTarget, State>(
        operation = Operation.Noop(),
        dragToProgress = { 0f },
        partial = { _, _ -> Offset(0f, 0f) }
    )
}
