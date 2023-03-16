package com.bumble.appyx.interactions.core.model.backpresshandlerstrategies

import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.model.transition.TransitionModel

abstract class BaseBackPressHandlerStrategy<InteractionTarget : Any, State : Any>
    : BackPressHandlerStrategy<InteractionTarget, State> {

    protected lateinit var interactionModel: InteractionModel<InteractionTarget, State>
    protected lateinit var transitionModel: TransitionModel<InteractionTarget, State>

    override fun init(
        interactionModel: InteractionModel<InteractionTarget, State>,
        transitionModel: TransitionModel<InteractionTarget, State>
    ) {
        this.interactionModel = interactionModel
        this.transitionModel = transitionModel
    }
}
