package com.bumble.appyx.interactions.core.model.progress

import com.bumble.appyx.interactions.core.model.transition.Operation

interface ProgressController<NavTarget, ModelState> {

    fun operation(operation: Operation<ModelState>)
}
