package com.bumble.appyx.interactions.core.model.progress

import com.bumble.appyx.interactions.core.model.transition.Operation

interface ProgressController<InteractionTarget, ModelState> {

    fun operation(operation: Operation<ModelState>)
}
