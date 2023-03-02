package com.bumble.appyx.interactions.core.ui

import com.bumble.appyx.interactions.core.Element

data class ScreenState<InteractionTarget>(
    val onScreen: Set<Element<InteractionTarget>> = emptySet(),
    val offScreen: Set<Element<InteractionTarget>> = emptySet()
)
