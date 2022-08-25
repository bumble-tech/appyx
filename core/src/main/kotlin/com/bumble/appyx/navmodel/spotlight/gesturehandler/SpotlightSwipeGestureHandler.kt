package com.bumble.appyx.navmodel.spotlight.gesturehandler

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.bumble.appyx.core.navigation.gesture.GestureHandler
import com.bumble.appyx.core.navigation.gesture.swipe.SwipeGesture
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.operation.next
import com.bumble.appyx.navmodel.spotlight.operation.previous

class SpotlightSwipeGestureHandler<Routing : Any>(
    private val spotlight: Spotlight<Routing>
) : GestureHandler<SwipeGesture> {

    override fun handleGesture(gesture: SwipeGesture) {
        return when (gesture) {
            is SwipeGesture.SwipeRight -> spotlight.next()
            is SwipeGesture.SwipeLeft -> spotlight.previous()
            else -> Unit
        }
    }
}

@Composable
fun <Routing : Any> rememberSpotlightSwipeGestureHandler(spotlight: Spotlight<Routing>) = remember {
    SpotlightSwipeGestureHandler(spotlight)
}
