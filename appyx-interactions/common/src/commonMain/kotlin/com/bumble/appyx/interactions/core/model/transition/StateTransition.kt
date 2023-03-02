package com.bumble.appyx.interactions.core.model.transition

data class StateTransition<State>(
    val fromState: State,
    val targetState: State
)
