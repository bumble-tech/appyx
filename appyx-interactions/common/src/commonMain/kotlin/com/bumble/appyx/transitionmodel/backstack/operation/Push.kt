package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel

/**
 * Operation:
 *
 * [A, B, C] + Push(D) = [A, B, C, D]
 */
@Parcelize
data class Push<InteractionTarget : Any>(
    private val interactionTarget: @RawValue InteractionTarget,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<BackStackModel.State<InteractionTarget>>() {

    override fun isApplicable(state: BackStackModel.State<InteractionTarget>): Boolean =
        interactionTarget != state.active.interactionTarget

    override fun createFromState(baseLineState: BackStackModel.State<InteractionTarget>): BackStackModel.State<InteractionTarget> =
        baseLineState.copy(
            created = baseLineState.created + interactionTarget.asElement()
        )

    override fun createTargetState(fromState: BackStackModel.State<InteractionTarget>): BackStackModel.State<InteractionTarget> =
        fromState.copy(
            active = fromState.created.last(),
            created = fromState.created.dropLast(1),
            stashed = fromState.stashed + fromState.active,
        )
}

fun <InteractionTarget : Any> BackStack<InteractionTarget>.push(
    interactionTarget: InteractionTarget,
    mode: Operation.Mode = Operation.Mode.KEYFRAME
) {
    operation(Push(interactionTarget, mode))
}
