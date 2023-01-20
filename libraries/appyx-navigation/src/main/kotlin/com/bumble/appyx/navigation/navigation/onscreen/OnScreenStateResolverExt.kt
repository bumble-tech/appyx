package com.bumble.appyx.navigation.navigation.onscreen

import com.bumble.appyx.interactions.core.NavElement

fun <State> OnScreenStateResolver<State>.isOnScreen(element: NavElement<*, out State>): Boolean =
    if (element.transitionHistory.isEmpty()) {
        isOnScreen(element.fromState) || isOnScreen(element.state)
    } else {
        element.transitionHistory.any { (fromState, targetState) ->
            isOnScreen(fromState) || isOnScreen(targetState)
        }
    }
