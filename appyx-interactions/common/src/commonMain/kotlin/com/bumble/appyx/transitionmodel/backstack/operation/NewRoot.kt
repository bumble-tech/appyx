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
 * [A, B, C] + NewRoot(D) = [ D ]
 */
@Parcelize
data class NewRoot<InteractionTarget>(
    private val interactionTarget: @RawValue InteractionTarget,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<BackStackModel.State<InteractionTarget>>() {
    override fun isApplicable(state: BackStackModel.State<InteractionTarget>): Boolean =
        true

    override fun createFromState(baseLineState: BackStackModel.State<InteractionTarget>): BackStackModel.State<InteractionTarget> =
        baseLineState.copy(
            created = baseLineState.created + interactionTarget.asElement()
        )

    override fun createTargetState(fromState: BackStackModel.State<InteractionTarget>): BackStackModel.State<InteractionTarget> =
        fromState.copy(
            active = fromState.created.last(),
            created = fromState.created.dropLast(1),
            destroyed = fromState.destroyed + fromState.stashed,
            stashed = emptyList()
        )
}

fun <InteractionTarget : Any> BackStack<InteractionTarget>.newRoot(
    interactionTarget: InteractionTarget,
    mode: Operation.Mode = Operation.Mode.KEYFRAME
) {
    operation(NewRoot(interactionTarget, mode))
}
