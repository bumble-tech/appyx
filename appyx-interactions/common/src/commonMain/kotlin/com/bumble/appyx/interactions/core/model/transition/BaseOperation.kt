package com.bumble.appyx.interactions.core.model.transition

abstract class BaseOperation<ModelState> : Operation<ModelState> {

    final override fun invoke(state: ModelState): StateTransition<ModelState> {
        val fromState = createFromState(state)
        val targetState = createTargetState(fromState)

        return StateTransition(
            fromState = fromState,
            targetState = targetState
        )
    }

    /**
     * @return ModelState If the operation adds any new elements to the scene,
     *                    it MUST add them to the state here.
     *                    Otherwise, just return baseLineState unchanged.
     */
    abstract fun createFromState(baseLineState: ModelState): ModelState

    /**
     * @return ModelState Any elements present in this state
     *                    MUST also be present in the fromState already.
     */
    abstract fun createTargetState(fromState: ModelState): ModelState
}
