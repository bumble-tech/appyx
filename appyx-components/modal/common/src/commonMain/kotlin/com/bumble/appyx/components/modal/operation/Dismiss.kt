package com.bumble.appyx.components.modal.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.modal.Modal
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation

@Parcelize
class Dismiss<InteractionTarget : Any>(
    override var mode: Operation.Mode = Operation.Mode.IMMEDIATE
) : BaseOperation<ModalModel.State<InteractionTarget>>() {

    override fun isApplicable(state: ModalModel.State<InteractionTarget>) = state.modal != null || state.fullScreen != null

    override fun createTargetState(fromState: ModalModel.State<InteractionTarget>) =
        if (fromState.created.isNotEmpty()) {
            fromState.copy(
                created = fromState.created.dropLast(1),
                modal = fromState.created.last(),
                fullScreen = null,
                destroyed = fromState.destroyed + listOfNotNull(
                    fromState.modal,
                    fromState.fullScreen
                )
            )
        } else {
            fromState.copy(
                modal = null,
                fullScreen = null,
                destroyed = fromState.destroyed + listOfNotNull(
                    fromState.modal,
                    fromState.fullScreen
                )
            )
        }

    override fun createFromState(baseLineState: ModalModel.State<InteractionTarget>) = baseLineState
}

fun <InteractionTarget : Any> Modal<InteractionTarget>.dismiss(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMMEDIATE
) {
    operation(
        operation = Dismiss(
            mode = mode
        ),
        animationSpec = animationSpec,
    )
}
