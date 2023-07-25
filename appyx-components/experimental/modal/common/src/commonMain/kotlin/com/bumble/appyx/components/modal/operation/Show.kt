package com.bumble.appyx.components.modal.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.modal.Modal
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class Show<InteractionTarget : Any>(
    override var mode: Operation.Mode = Operation.Mode.IMMEDIATE
) : BaseOperation<ModalModel.State<InteractionTarget>>() {

    override fun isApplicable(state: ModalModel.State<InteractionTarget>) =
        state.created.isNotEmpty()

    override fun createFromState(baseLineState: ModalModel.State<InteractionTarget>) = baseLineState

    override fun createTargetState(fromState: ModalModel.State<InteractionTarget>) =
        fromState.copy(
            created = fromState.created.dropLast(1),
            modal = fromState.created.last(),
            fullScreen = null,
            destroyed = fromState.destroyed + listOfNotNull(fromState.modal, fromState.fullScreen)
        )
}

fun <InteractionTarget : Any> Modal<InteractionTarget>.show(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMMEDIATE
) {
    operation(
        operation = Show(
            mode = mode
        ),
        animationSpec = animationSpec,
    )
}
