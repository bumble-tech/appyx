@file:Suppress("TransitionPropertiesLabel")

package com.github.zsoltk.composeribs

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.RoutingTransitionState.*


// Defined by RoutingSource, this one would be for BackStack:
enum class RoutingTransitionState {
    ADDED, ACTIVE, INACTIVE, REMOVED
}

// Client code
sealed class Routing {
    object Whatever : Routing()
}

// Framework code
@Composable
fun Random() {
    // []
    // [A:ADDED]
    // [A:ACTIVE]
    // [A:INACTIVE, B:ADDED]
    // [A:INACTIVE, B:ACTIVE]
    // [A:ACTIVE, B:REMOVED] TODO flag to keep or just different RoutingSource which supports additional state?
    // [A:ACTIVE]

    // TODO here or parent scope: remember(once per routing change)

    // Imagine this block automated for each element, for example upon creation:

    // Client -> Framework
    val routing: Routing = Routing.Whatever
    // Framework
    val currentState = remember { MutableTransitionState(ADDED) }
    currentState.targetState = ACTIVE
    val transition: Transition<RoutingTransitionState> = updateTransition(currentState)
    // Client -> Framework
    val modifier = mapper(routing = routing, transition = transition)
    // TODO use modifier under the hood in RibView.Compose()

    // Cleanup
    when (transition.currentState) {
        INACTIVE -> {} // TODO callback to set ChildEntry.onScreen to false
        REMOVED -> {} // TODO callback to actually remove element
        else -> {}
    }
}

// This is equivalent to TransitionHandler
// - part of client code
// - but we can provide open classes for default implementation too
@Composable
fun mapper(routing: Routing, transition: Transition<RoutingTransitionState>): Modifier {

    // or any other thing based on transition
    val progress = transition.animateFloat {
        when (it) {
            ADDED -> 0f
            ACTIVE -> 1f
            INACTIVE -> 0f
            REMOVED -> 0f
        }
    }

    return Modifier // todo factor in `progress` or any other thing based on transition
}



@Composable
fun Child1View() {

}
