package com.bumble.appyx.interactions.core.model.backpresshandlerstrategies

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DontHandleBackPress<NavTarget : Any, State : Any> :
    BaseBackPressHandlerStrategy<NavTarget, State>() {

    override val canHandleBackPress: Flow<Boolean> = flowOf(false)
    override fun handleUpNavigation(): Boolean = false
}
