@file:Suppress("TransitionPropertiesLabel")

package com.github.zsoltk.composeribs

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier


// Defined by RoutingSource, this one would be for BackStack:
enum class RoutingState {
    REMOVED, CREATED, STASHED, RESTORED
}

// Client code
sealed class Routing {
    object Whatever : Routing()
}

// Framework code
@Composable
fun Random() {
    // []
    // [A:REMOVED]
    // [A:CREATED]
    // [A:STASHED, B:REMOVED]
    // [A:STASHED, B:CREATED]
    // [A:STASHED, B:REMOVED]
    // [A:RESTORED]

    // TODO here or parent scope: remember(once per routing change)

    // Imagine this block automated for each element, for example upon creation:

    // Client -> Framework
    val routing: Routing = Routing.Whatever
    // Framework
    val currentState = remember { MutableTransitionState(RoutingState.REMOVED) }
    currentState.targetState = RoutingState.CREATED
    val transition: Transition<RoutingState> = updateTransition(currentState)
    // Client -> Framework
    val modifier = mapper(routing = routing, transition = transition)

    // TODO use modifier under the hood in RibView.Compose()
}

// This is equivalent to TransitionHandler
// - part of client code
// - but we can provide open classes for default implementation too
@Composable
fun mapper(routing: Routing, transition: Transition<RoutingState>): Modifier {

    // or any other thing based on transition
    val progress = transition.animateFloat {
        when (it) {
            RoutingState.REMOVED -> 0f
            RoutingState.CREATED -> 1f
            RoutingState.STASHED -> 0f
            RoutingState.RESTORED -> 1f
        }
    }

    return Modifier // todo factor in `progress` or any other thing based on transition
}



@Composable
fun Child1View() {

}
