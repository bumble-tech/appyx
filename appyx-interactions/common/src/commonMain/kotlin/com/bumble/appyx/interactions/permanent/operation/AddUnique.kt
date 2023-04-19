package com.bumble.appyx.interactions.permanent.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.permanent.PermanentInteractionModel
import com.bumble.appyx.interactions.permanent.PermanentModel

@Parcelize
data class AddUnique<InteractionTarget : Any>(
    private val interactionTarget: @RawValue InteractionTarget,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<PermanentModel.State<InteractionTarget>>() {


    override fun isApplicable(state: PermanentModel.State<InteractionTarget>): Boolean =
        !state.elements.any { it.interactionTarget == interactionTarget }

    override fun createFromState(baseLineState: PermanentModel.State<InteractionTarget>): PermanentModel.State<InteractionTarget> =
        baseLineState

    override fun createTargetState(fromState: PermanentModel.State<InteractionTarget>): PermanentModel.State<InteractionTarget> =
        fromState.copy(
            elements = fromState.elements + interactionTarget.asElement()
        )
}

fun <InteractionTarget : Any> PermanentInteractionModel<InteractionTarget>.addUnique(
    interactionTarget: InteractionTarget
) {
    operation(operation = AddUnique(interactionTarget))
}
