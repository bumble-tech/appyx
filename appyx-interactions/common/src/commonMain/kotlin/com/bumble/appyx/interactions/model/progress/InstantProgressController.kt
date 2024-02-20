package com.bumble.appyx.interactions.model.progress

import com.bumble.appyx.interactions.model.transition.Keyframes
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.interactions.model.transition.TransitionModel
import com.bumble.appyx.interactions.model.transition.TransitionModel.SettleDirection.COMPLETE

class InstantProgressController<InteractionTarget : Any, ModelState>(
    private val model: TransitionModel<InteractionTarget, ModelState>,
) : ProgressController<InteractionTarget, ModelState> {


    override fun operation(operation: Operation<ModelState>) {
        model.operation(operation)
        val currentState = model.output.value
        if (currentState is Keyframes<ModelState>) {
            model.setProgress(progress = currentState.maxProgress)
            model.onSettled(direction = COMPLETE, animate = false)
        }
    }
}
