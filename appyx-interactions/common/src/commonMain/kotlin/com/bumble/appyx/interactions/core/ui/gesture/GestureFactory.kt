package com.bumble.appyx.interactions.core.ui.gesture

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density

interface GestureFactory<InteractionTarget, ModelState> {

    /**
     * isContinuous: Boolean - indicates that if during a drag gesture this operation completes
     * but there's still offset to process a new gesture will be created that handles the remaining
     * amount.
     */
    val isContinuous: Boolean
        get() = true

    fun onStartDrag(position: Offset) {}

    fun createGesture(
        state: ModelState,
        delta: Offset,
        density: Density
    ): Gesture<InteractionTarget, ModelState>

    class Noop<InteractionTarget, ModelState> : GestureFactory<InteractionTarget, ModelState> {
        override fun createGesture(
            state: ModelState,
            delta: Offset,
            density: Density
        ): Gesture<InteractionTarget, ModelState> =
            Gesture.Noop()
    }
}
