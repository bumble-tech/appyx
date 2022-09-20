package com.bumble.appyx.core.navigation.backpresshandlerstrategies

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DontHandleBackPress<NavTarget, TransitionState> :
    BaseBackPressHandlerStrategy<NavTarget, TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> =
        flowOf(false)

    override fun onBackPressed() {
        // Noop
    }
}
