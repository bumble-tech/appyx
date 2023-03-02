package com.bumble.appyx.interactions.core.progress

import com.bumble.appyx.interactions.core.Operation

interface ProgressController<NavTarget, ModelState> {

    fun operation(operation: Operation<ModelState>)
}
