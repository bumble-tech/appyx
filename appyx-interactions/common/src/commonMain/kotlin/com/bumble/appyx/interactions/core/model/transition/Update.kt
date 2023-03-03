package com.bumble.appyx.interactions.core.model.transition

data class Update<ModelState>(
    override val currentTargetState: ModelState,
    val animate: Boolean = true
) : TransitionModel.Output<ModelState>() {

    override val lastTargetState: ModelState =
        currentTargetState

    override fun deriveUpdate(stateTransition: StateTransition<ModelState>): Update<ModelState> =
        Update(
            currentTargetState = stateTransition.targetState
        )

    override fun deriveKeyframes(stateTransition: StateTransition<ModelState>): Keyframes<ModelState> =
        Keyframes(
            queue = listOf(
                Segment(
                    stateTransition = stateTransition,
                )
            )
        )
}
