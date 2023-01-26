package com.bumble.appyx.interactions.core.inputsource

import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.TransitionModel

class InstantInputSource<NavTarget : Any, ModelState>(
    private val model: TransitionModel<NavTarget, ModelState>,
) : InputSource<NavTarget, ModelState> {


    override fun operation(operation: Operation<ModelState>) {
        model.operation(operation)
        model.setProgress(progress = model.maxProgress)
    }
}
