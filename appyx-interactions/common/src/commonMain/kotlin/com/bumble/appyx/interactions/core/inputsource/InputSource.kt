package com.bumble.appyx.interactions.core.inputsource

import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.TransitionModel.OperationMode
import com.bumble.appyx.interactions.core.TransitionModel.OperationMode.ENQUEUE

interface InputSource<NavTarget, ModelState> {

    fun operation(operation: Operation<ModelState>, mode: OperationMode = ENQUEUE)
}
