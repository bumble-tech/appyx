package com.bumble.appyx.interactions.core.ui.gesture

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density

interface GestureFactory<InteractionTarget, State> {

    fun onStartDrag(position: Offset) {}

    fun createGesture(delta: Offset, density: Density): Gesture<InteractionTarget, State>

    class Noop<InteractionTarget, State> : GestureFactory<InteractionTarget, State> {
        override fun createGesture(delta: Offset, density: Density): Gesture<InteractionTarget, State> =
            Gesture.Noop()
    }
}
