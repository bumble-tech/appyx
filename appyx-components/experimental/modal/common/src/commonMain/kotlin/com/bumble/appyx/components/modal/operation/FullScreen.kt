package com.bumble.appyx.components.modal.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.modal.Modal
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class FullScreen<InteractionTarget : Any>(
    override var mode: Operation.Mode = Operation.Mode.IMMEDIATE
) : BaseOperation<ModalModel.State<InteractionTarget>>() {

    override fun isApplicable(state: ModalModel.State<InteractionTarget>) = state.modal != null

    override fun createFromState(baseLineState: ModalModel.State<InteractionTarget>) =
        baseLineState

    override fun createTargetState(fromState: ModalModel.State<InteractionTarget>) =
        fromState.copy(
            modal = null,
            fullScreen = fromState.modal
        )
}

fun <InteractionTarget : Any> Modal<InteractionTarget>.fullScreen(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMMEDIATE
) {
    operation(
        operation = FullScreen(
            mode = mode
        ),
        animationSpec = animationSpec,
    )
}
