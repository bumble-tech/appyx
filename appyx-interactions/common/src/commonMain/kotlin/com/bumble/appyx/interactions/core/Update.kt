package com.bumble.appyx.interactions.core

data class Update<ModelState>(
    val history: List<ModelState> = emptyList(),
    override val currentTargetState: ModelState,
    val animate: Boolean = true
) : TransitionModel.Output<ModelState>() {

    override val lastTargetState: ModelState =
        currentTargetState

    override fun replace(targetState: ModelState): TransitionModel.Output<ModelState> =
        Update(
            history = history,
            currentTargetState = targetState
        )

    override fun deriveUpdate(navTransition: NavTransition<ModelState>): Update<ModelState> =
        Update(
            history = history + listOf(),
            currentTargetState = navTransition.targetState
        )

    override fun deriveKeyframes(navTransition: NavTransition<ModelState>): Keyframes<ModelState> =
        Keyframes(
            queue = listOf(
                Segment(
                    navTransition = navTransition,
                )
            )
        )
}
