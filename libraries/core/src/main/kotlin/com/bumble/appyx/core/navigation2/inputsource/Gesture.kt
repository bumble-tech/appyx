package com.bumble.appyx.core.navigation2.inputsource

import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.core.navigation2.Operation

class Gesture<NavTarget, State>(
    val operation: Operation<NavTarget, State>,
    val dragToProgress: (Offset) -> Float
) {
    var startProgress: Float? = null
}
