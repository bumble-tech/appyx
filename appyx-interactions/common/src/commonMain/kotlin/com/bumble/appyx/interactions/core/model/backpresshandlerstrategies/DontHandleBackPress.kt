package com.bumble.appyx.interactions.core.model.backpresshandlerstrategies

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DontHandleBackPress<InteractionTarget : Any, State : Any> :
    BaseBackPressHandlerStrategy<InteractionTarget, State>() {

    override val canHandleBackPress: StateFlow<Boolean> = MutableStateFlow(false)
    override fun handleBackPress(): Boolean = false
}
