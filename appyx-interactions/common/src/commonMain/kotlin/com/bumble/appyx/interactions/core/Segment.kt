package com.bumble.appyx.interactions.core

data class Segment<ModelState>(
    val navTransition: NavTransition<ModelState>,
) {

    val fromState: ModelState
        get() = navTransition.fromState

    val targetState: ModelState
        get() = navTransition.targetState

    fun replace(targetState: ModelState): Segment<ModelState> =
        copy(
            navTransition = navTransition.copy(
                targetState = targetState
            )
        )
}
