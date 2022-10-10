package com.bumble.appyx.core.navigation2.inputsource

import com.bumble.appyx.core.navigation2.Operation
import com.bumble.appyx.core.navigation2.BaseNavModel

class InstantInputSource<NavTarget : Any, State>(
    private val navModel: BaseNavModel<NavTarget, State>,
) : InputSource<NavTarget, State> {

    override fun operation(operation: Operation<NavTarget, State>) {
        navModel.enqueue(operation)
        navModel.setProgress(progress = navModel.maxProgress)
    }
}
