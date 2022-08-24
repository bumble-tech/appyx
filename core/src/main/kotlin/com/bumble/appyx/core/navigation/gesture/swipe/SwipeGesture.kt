package com.bumble.appyx.core.navigation.gesture.swipe

import com.bumble.appyx.core.navigation.gesture.Gesture

sealed class SwipeGesture : Gesture {
    object SwipeRight : SwipeGesture()
    object SwipeLeft : SwipeGesture()
    object SwipeUp : SwipeGesture()
    object SwipeDown : SwipeGesture()
}
