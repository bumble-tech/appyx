package com.bumble.appyx.core.navigation2

data class NavTransition<NavTarget, State>(
    val fromState: NavElements<NavTarget, State>,
    val targetState: NavElements<NavTarget, State>
)
