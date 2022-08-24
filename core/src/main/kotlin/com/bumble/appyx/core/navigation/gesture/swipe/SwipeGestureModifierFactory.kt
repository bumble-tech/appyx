package com.bumble.appyx.core.navigation.gesture.swipe

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import com.bumble.appyx.core.navigation.gesture.GestureHandler

fun Modifier.swipeNavigationModifier(
    gestureHandler: GestureHandler<SwipeGesture>,
    thresholdSwipeRight: Float = SwipeThreshold,
    thresholdSwipeLeft: Float = SwipeThreshold,
    thresholdSwipeUp: Float = SwipeThreshold,
    thresholdSwipeDown: Float = SwipeThreshold,
): Modifier {
    return this.composed {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        pointerInput(Unit) {
            detectDragGestures(
                onDragEnd = {
                    if (offsetX > thresholdSwipeLeft) {
                        gestureHandler.handleGesture(SwipeGesture.SwipeLeft)
                    } else if (offsetX < -thresholdSwipeRight) {
                        gestureHandler.handleGesture(SwipeGesture.SwipeRight)
                    }

                    if (offsetY > thresholdSwipeDown) {
                        gestureHandler.handleGesture(SwipeGesture.SwipeDown)
                    } else if (offsetY < -thresholdSwipeUp) {
                        gestureHandler.handleGesture(SwipeGesture.SwipeUp)
                    }
                },
                onDragStart = {
                    offsetX = 0f
                    offsetY = 0f
                }
            ) { _, dragAmount ->
                val (x, y) = dragAmount
                offsetX += x
                offsetY += y
            }
        }
    }
}

private const val SwipeThreshold = 200f
