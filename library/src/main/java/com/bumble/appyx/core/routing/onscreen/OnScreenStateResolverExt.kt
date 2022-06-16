package com.bumble.appyx.core.routing.onscreen

import com.bumble.appyx.core.routing.RoutingElement

fun <State> OnScreenStateResolver<State>.isOnScreen(element: RoutingElement<*, State>): Boolean =
    if (element.transitionHistory.isEmpty()) {
        isOnScreen(element.fromState) || isOnScreen(element.targetState)
    } else {
        element.transitionHistory.any { (fromState, targetState) ->
            isOnScreen(fromState) || isOnScreen(targetState)
        }
    }
