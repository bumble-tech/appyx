package com.bumble.appyx.interactions.core.model.backpresshandlerstrategies

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DontHandleBackPress<InteractionTarget : Any, State : Any> :
    BaseBackPressHandlerStrategy<InteractionTarget, State>() {

    override val canHandleBackPress: Flow<Boolean> = flowOf(false)
    override fun handleUpNavigation(): Boolean = false
}
