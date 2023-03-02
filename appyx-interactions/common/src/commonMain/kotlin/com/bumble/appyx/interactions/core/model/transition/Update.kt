package com.bumble.appyx.interactions.core.model.transition

data class Update<ModelState>(
    override val currentTargetState: ModelState,
    val animate: Boolean = true
) : TransitionModel.Output<ModelState>() {

    override val lastTargetState: ModelState =
        currentTargetState

    override fun replace(targetState: ModelState): TransitionModel.Output<ModelState> =
        Update(
            currentTargetState = targetState
        )

    override fun deriveUpdate(navTransition: NavTransition<ModelState>): Update<ModelState> =
        Update(
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
