package com.bumble.appyx.interactions.core.ui

import com.bumble.appyx.interactions.core.NavElement

data class ScreenState<NavTarget>(
    val onScreen: Set<NavElement<NavTarget>> = emptySet(),
    val offScreen: Set<NavElement<NavTarget>> = emptySet()
)
