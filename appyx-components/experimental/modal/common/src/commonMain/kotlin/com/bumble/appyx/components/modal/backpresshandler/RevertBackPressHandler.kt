package com.bumble.appyx.components.modal.backpresshandler

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.components.modal.operation.Revert
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.mapState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class RevertBackPressHandler<InteractionTarget : Any>(
    val scope: CoroutineScope,
    val animationSpec: AnimationSpec<Float>? = null
) : BaseBackPressHandlerStrategy<InteractionTarget, ModalModel.State<InteractionTarget>>() {

    override val canHandleBackPress: StateFlow<Boolean> by lazy {
        transitionModel.output.mapState(scope) { output ->
            output.currentTargetState.fullScreen != null
        }
    }

    override fun handleBackPress(): Boolean {
        return if (canHandleBackPress.value) {
            interactionModel.operation(operation = Revert(), animationSpec)
            true
        } else false
    }
}
