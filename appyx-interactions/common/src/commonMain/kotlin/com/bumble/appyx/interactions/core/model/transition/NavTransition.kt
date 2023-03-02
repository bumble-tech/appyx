package com.bumble.appyx.interactions.core.model.transition

data class NavTransition<State>(
    val fromState: State,
    val targetState: State
)
