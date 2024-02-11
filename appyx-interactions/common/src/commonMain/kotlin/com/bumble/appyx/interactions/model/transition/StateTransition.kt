package com.bumble.appyx.interactions.model.transition

data class StateTransition<ModelState>(
    val fromState: ModelState,
    val targetState: ModelState
)
