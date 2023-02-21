package com.bumble.appyx.interactions.core.backpresshandlerstrategies

import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.TransitionModel
import kotlinx.coroutines.flow.Flow

interface BackPressHandlerStrategy<NavTarget : Any, State : Any> {
    fun init(
        interactionModel: InteractionModel<NavTarget, State>,
        transitionModel: TransitionModel<NavTarget, State>
    )

    val canHandleBackPress: Flow<Boolean>
    fun handleUpNavigation(): Boolean
}
