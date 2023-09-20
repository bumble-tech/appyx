package com.bumble.appyx.components.modal.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.modal.Modal
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue

@Parcelize
class Add<InteractionTarget : Any>(
    private val interactionTarget: @RawValue InteractionTarget,
    override var mode: Operation.Mode = Operation.Mode.IMMEDIATE
) : BaseOperation<ModalModel.State<InteractionTarget>>() {

    override fun isApplicable(state: ModalModel.State<InteractionTarget>) = true

    override fun createFromState(baseLineState: ModalModel.State<InteractionTarget>) =
        baseLineState.copy(
            created = baseLineState.created + interactionTarget.asElement()
        )

    override fun createTargetState(fromState: ModalModel.State<InteractionTarget>) = fromState
}

fun <InteractionTarget : Any> Modal<InteractionTarget>.add(
    item: InteractionTarget,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.IMMEDIATE
) {
    operation(
        operation = Add(
            interactionTarget = item,
            mode = mode
        ),
        animationSpec = animationSpec,
    )
}
