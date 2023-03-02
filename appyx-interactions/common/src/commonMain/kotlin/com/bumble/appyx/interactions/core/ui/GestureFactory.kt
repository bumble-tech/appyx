package com.bumble.appyx.interactions.core.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.core.progress.Gesture

interface GestureFactory<NavTarget, State> {

    fun onStartDrag(position: Offset) {}

    fun createGesture(delta: Offset, density: Density): Gesture<NavTarget, State>

    class Noop<NavTarget, State> : GestureFactory<NavTarget, State> {
        override fun createGesture(delta: Offset, density: Density): Gesture<NavTarget, State> =
            Gesture.Noop()
    }
}
