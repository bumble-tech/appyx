package com.bumble.appyx.core.navigation.onscreen

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavElement

fun <State : Parcelable> OnScreenStateResolver<State>.isOnScreen(element: NavElement<*, out State>): Boolean =
    if (element.transitionHistory.isEmpty()) {
        isOnScreen(element.fromState) || isOnScreen(element.targetState)
    } else {
        element.transitionHistory.any { (fromState, targetState) ->
            isOnScreen(fromState) || isOnScreen(targetState)
        }
    }
