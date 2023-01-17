package com.bumble.appyx.interactions.core

data class NavTransition<NavTarget, State>(
    val fromState: NavElements<NavTarget, State>,
    val targetState: NavElements<NavTarget, State>
)
