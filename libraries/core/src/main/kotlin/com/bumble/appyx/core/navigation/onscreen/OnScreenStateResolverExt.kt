package com.bumble.appyx.core.navigation.onscreen

import com.bumble.appyx.core.navigation.NavElement

fun <State> OnScreenStateResolver<State>.isOnScreen(element: NavElement<*, out State>): Boolean =
    if (element.transitionHistory.isEmpty()) {
        isOnScreen(element.fromState) || isOnScreen(element.targetState)
    } else {
        element.transitionHistory.any { (fromState, targetState) ->
            isOnScreen(fromState) || isOnScreen(targetState)
        }
    }
