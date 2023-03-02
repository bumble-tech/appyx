package com.bumble.appyx.interactions.core.model.progress

import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.model.transition.TransitionModel

class InstantInputSource<InteractionTarget : Any, ModelState>(
    private val model: TransitionModel<InteractionTarget, ModelState>,
) : ProgressController<InteractionTarget, ModelState> {


    override fun operation(operation: Operation<ModelState>) {
        model.operation(operation)
        val currentState = model.output.value
        if (currentState is Keyframes<ModelState>) {
            model.setProgress(progress = currentState.maxProgress)
        }
    }
}
