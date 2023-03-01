package com.bumble.appyx.interactions.core.backpresshandlerstrategies

import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.TransitionModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DontHandleBackPress<NavTarget : Any, State : Any> :
    BaseBackPressHandlerStrategy<NavTarget, State>() {

    override val canHandleBackPress: Flow<Boolean> = flowOf(false)
    override fun handleUpNavigation(): Boolean = false
}
