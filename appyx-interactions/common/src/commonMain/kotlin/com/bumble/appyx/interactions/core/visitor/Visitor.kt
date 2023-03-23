package com.bumble.appyx.interactions.core.visitor

import com.bumble.appyx.interactions.core.model.transition.TransitionModel

interface Visitor {

    fun <InteractionTarget, ModelState> visit(transitionModel: TransitionModel<InteractionTarget, ModelState>)
}
