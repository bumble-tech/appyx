package com.bumble.appyx.v2.core.routing

fun <State> OnScreenStateResolver<State>.isOnScreen(element: RoutingElement<*, State>): Boolean =
    if (element.transitionHistory.isEmpty()) {
        isOnScreen(element.fromState) || isOnScreen(element.targetState)
    } else {
        element.transitionHistory.any { (fromState, targetState) ->
            isOnScreen(fromState) || isOnScreen(targetState)
        }
    }
