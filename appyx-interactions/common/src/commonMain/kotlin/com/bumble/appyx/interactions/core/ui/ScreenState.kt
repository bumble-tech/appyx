package com.bumble.appyx.interactions.core.ui

import com.bumble.appyx.interactions.core.NavElement

data class ScreenState<InteractionTarget>(
    val onScreen: Set<NavElement<InteractionTarget>> = emptySet(),
    val offScreen: Set<NavElement<InteractionTarget>> = emptySet()
)
