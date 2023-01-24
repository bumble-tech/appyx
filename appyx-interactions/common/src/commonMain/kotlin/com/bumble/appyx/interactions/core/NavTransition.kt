package com.bumble.appyx.interactions.core

data class NavTransition<State>(
    val fromState: State,
    val targetState: State
)
