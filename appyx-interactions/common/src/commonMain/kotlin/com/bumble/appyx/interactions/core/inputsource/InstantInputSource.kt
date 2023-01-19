package com.bumble.appyx.interactions.core.inputsource

import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.TransitionModel

class InstantInputSource<NavTarget : Any, State>(
    private val model: TransitionModel<NavTarget, State>,
) : InputSource<NavTarget, State> {

    override fun operation(operation: Operation<NavTarget, State>) {
        model.enqueue(operation)
        model.setProgress(progress = model.maxProgress)
    }
}
