package com.bumble.appyx.core.navigation.gesture

import androidx.compose.runtime.Stable

@Stable
interface Gesture

@Stable
interface GestureHandler<GestureType : Gesture> {

    fun handleGesture(gesture: GestureType)
}
