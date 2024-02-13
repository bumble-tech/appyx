package com.bumble.appyx.interactions.model.progress

import com.bumble.appyx.interactions.model.transition.Operation

interface ProgressController<InteractionTarget, ModelState> {

    fun operation(operation: Operation<ModelState>)
}
