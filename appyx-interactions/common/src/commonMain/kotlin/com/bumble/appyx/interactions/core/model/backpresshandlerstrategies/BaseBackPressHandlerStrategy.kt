package com.bumble.appyx.interactions.core.model.backpresshandlerstrategies

import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.model.transition.TransitionModel

abstract class BaseBackPressHandlerStrategy<NavTarget : Any, State : Any>
    : BackPressHandlerStrategy<NavTarget, State> {

    protected lateinit var interactionModel: InteractionModel<NavTarget, State>
    protected lateinit var transitionModel: TransitionModel<NavTarget, State>

    override fun init(
        interactionModel: InteractionModel<NavTarget, State>,
        transitionModel: TransitionModel<NavTarget, State>
    ) {
        this.interactionModel = interactionModel
        this.transitionModel = transitionModel
    }
}
