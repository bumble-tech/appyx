package com.bumble.appyx.interactions.core.ui.onscreen

//import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.interactions.core.NavElement

fun <State> OnScreenStateResolver<State>.isOnScreen(element: NavElement<*, out State>): Boolean =
    if (element.transitionHistory.isEmpty()) {
        isOnScreen(element.fromState) || isOnScreen(element.state)
    } else {
        element.transitionHistory.any { (fromState, targetState) ->
            isOnScreen(fromState) || isOnScreen(targetState)
        }
    }
