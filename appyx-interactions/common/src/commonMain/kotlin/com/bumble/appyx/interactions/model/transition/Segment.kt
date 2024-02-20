package com.bumble.appyx.interactions.model.transition

data class Segment<ModelState>(
    val stateTransition: StateTransition<ModelState>,
) {

    val fromState: ModelState
        get() = stateTransition.fromState

    val targetState: ModelState
        get() = stateTransition.targetState
}
