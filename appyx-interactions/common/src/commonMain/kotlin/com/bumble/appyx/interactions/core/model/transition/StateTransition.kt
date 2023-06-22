package com.bumble.appyx.interactions.core.model.transition

data class StateTransition<ModelState>(
    val fromState: ModelState,
    val targetState: ModelState
)
