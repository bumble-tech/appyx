package com.bumble.appyx.interactions.model.backpresshandlerstrategies

import com.bumble.appyx.interactions.model.BaseAppyxComponent
import com.bumble.appyx.interactions.model.transition.TransitionModel
import kotlinx.coroutines.flow.StateFlow

interface BackPressHandlerStrategy<InteractionTarget : Any, State : Any> {

    fun init(
        appyxComponent: BaseAppyxComponent<InteractionTarget, State>,
        transitionModel: TransitionModel<InteractionTarget, State>
    )

    val canHandleBackPress: StateFlow<Boolean>

    fun handleBackPress(): Boolean
}
