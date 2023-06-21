package com.bumble.appyx.components.modal.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.modal.Modal
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation

@Parcelize
class Revert<InteractionTarget : Any>(
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<ModalModel.State<InteractionTarget>>() {

    override fun isApplicable(state: ModalModel.State<InteractionTarget>) = true

    override fun createTargetState(fromState: ModalModel.State<InteractionTarget>) =
        if (fromState.fullScreen != null || fromState.modal != null) {
            fromState.copy(
                modal = fromState.fullScreen,
                fullScreen = fromState.modal
            )
        } else fromState

    override fun createFromState(baseLineState: ModalModel.State<InteractionTarget>) = baseLineState
}

fun <InteractionTarget : Any> Modal<InteractionTarget>.revert(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.KEYFRAME
) {
    operation(
        operation = Revert(
            mode = mode
        ),
        animationSpec = animationSpec,
    )
}