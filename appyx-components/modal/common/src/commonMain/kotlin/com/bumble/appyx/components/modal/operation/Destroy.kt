package com.bumble.appyx.components.modal.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.modal.Modal
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation

@Parcelize
class Destroy<InteractionTarget : Any>(
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<ModalModel.State<InteractionTarget>>() {

    override fun isApplicable(state: ModalModel.State<InteractionTarget>) = true

    override fun createTargetState(fromState: ModalModel.State<InteractionTarget>) =
        fromState.copy(
            created = emptyList(),
            modal = null,
            fullScreen = null,
            destroyed = fromState.destroyed + fromState.created + listOfNotNull(
                fromState.modal,
                fromState.fullScreen
            ),
        )

    override fun createFromState(baseLineState: ModalModel.State<InteractionTarget>) =
        baseLineState
}

fun <InteractionTarget : Any> Modal<InteractionTarget>.destroyAll(
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.KEYFRAME
) {
    operation(
        operation = Destroy(
            mode = mode
        ),
        animationSpec = animationSpec,
    )
}