package com.bumble.appyx.interactions.permanent.operation

import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.interactions.model.transition.BaseOperation
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.interactions.permanent.PermanentAppyxComponent
import com.bumble.appyx.interactions.permanent.PermanentModel
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue

@Parcelize
data class AddUnique<InteractionTarget : Any>(
    private val interactionTarget: @RawValue InteractionTarget,
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<PermanentModel.State<InteractionTarget>>() {


    override fun isApplicable(state: PermanentModel.State<InteractionTarget>): Boolean =
        !state.elements.any { it.interactionTarget == interactionTarget }

    override fun createFromState(
        baseLineState: PermanentModel.State<InteractionTarget>
    ): PermanentModel.State<InteractionTarget> =
        baseLineState

    override fun createTargetState(
        fromState: PermanentModel.State<InteractionTarget>
    ): PermanentModel.State<InteractionTarget> =
        fromState.copy(
            elements = fromState.elements + interactionTarget.asElement()
        )
}

fun <InteractionTarget : Any> PermanentAppyxComponent<InteractionTarget>.addUnique(
    interactionTarget: InteractionTarget
) {
    operation(operation = AddUnique(interactionTarget))
}
