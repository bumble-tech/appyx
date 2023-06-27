package com.bumble.appyx.interactions.core.model.backpresshandlerstrategies

import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.model.transition.TransitionModel

abstract class BaseBackPressHandlerStrategy<InteractionTarget : Any, State : Any>
    : BackPressHandlerStrategy<InteractionTarget, State> {

    protected lateinit var appyxComponent: BaseAppyxComponent<InteractionTarget, State>
    protected lateinit var transitionModel: TransitionModel<InteractionTarget, State>

    override fun init(
        appyxComponent: BaseAppyxComponent<InteractionTarget, State>,
        transitionModel: TransitionModel<InteractionTarget, State>
    ) {
        this.appyxComponent = appyxComponent
        this.transitionModel = transitionModel
    }
}
