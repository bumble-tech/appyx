package com.bumble.appyx.interactions.core.model.backpresshandlerstrategies

import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import kotlinx.coroutines.flow.Flow

interface BackPressHandlerStrategy<NavTarget : Any, State : Any> {
    fun init(
        interactionModel: InteractionModel<NavTarget, State>,
        transitionModel: TransitionModel<NavTarget, State>
    )

    val canHandleBackPress: Flow<Boolean>
    fun handleUpNavigation(): Boolean
}
