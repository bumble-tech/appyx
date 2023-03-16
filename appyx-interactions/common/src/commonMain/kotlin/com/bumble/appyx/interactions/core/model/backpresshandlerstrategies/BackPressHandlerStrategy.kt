package com.bumble.appyx.interactions.core.model.backpresshandlerstrategies

import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import kotlinx.coroutines.flow.Flow

interface BackPressHandlerStrategy<InteractionTarget : Any, State : Any> {
    fun init(
        interactionModel: InteractionModel<InteractionTarget, State>,
        transitionModel: TransitionModel<InteractionTarget, State>
    )

    val canHandleBackPress: Flow<Boolean>

    fun handleBackPress(): Boolean
}
