package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
@Parcelize
class Pop<InteractionTarget : Any>(
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<InteractionTarget>>() {
    override fun isApplicable(state: State<InteractionTarget>): Boolean =
        state.stashed.isNotEmpty()

    override fun createFromState(baseLineState: State<InteractionTarget>): State<InteractionTarget> =
        baseLineState


    override fun createTargetState(fromState: State<InteractionTarget>): State<InteractionTarget> =
        fromState.copy(
            active = fromState.stashed.last(),
            destroyed = fromState.destroyed + fromState.active,
            stashed = fromState.stashed.dropLast(1)
        )

    override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

    override fun hashCode(): Int = this.javaClass.hashCode()
}

fun <InteractionTarget : Any> BackStack<InteractionTarget>.pop(
    mode: Operation.Mode = Operation.Mode.KEYFRAME
) {
    operation(Pop(mode))
}
