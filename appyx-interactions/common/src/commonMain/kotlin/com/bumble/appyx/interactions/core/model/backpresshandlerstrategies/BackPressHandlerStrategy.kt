package com.bumble.appyx.interactions.core.model.backpresshandlerstrategies

import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import kotlinx.coroutines.flow.StateFlow

interface BackPressHandlerStrategy<InteractionTarget : Any, State : Any> {
    fun init(
        interactionModel: BaseInteractionModel<InteractionTarget, State>,
        transitionModel: TransitionModel<InteractionTarget, State>
    )

    val canHandleBackPress: StateFlow<Boolean>

    fun handleBackPress(): Boolean
}
