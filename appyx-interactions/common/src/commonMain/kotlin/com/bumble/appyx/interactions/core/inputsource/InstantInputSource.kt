package com.bumble.appyx.interactions.core.inputsource

import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.BaseTransitionModel

class InstantInputSource<NavTarget : Any, State>(
    private val navModel: BaseTransitionModel<NavTarget, State>,
) : InputSource<NavTarget, State> {

    override fun operation(operation: Operation<NavTarget, State>) {
        navModel.enqueue(operation)
        navModel.setProgress(progress = navModel.maxProgress)
    }
}
