package com.bumble.appyx.interactions.core.backpresshandlerstrategies

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DontHandleBackPress<NavTarget, State> :
    BaseBackPressHandlerStrategy<NavTarget, State>() {

    override val canHandleBackPressFlow: Flow<Boolean> =
        flowOf(false)

    override fun onBackPressed() {
        // Noop
    }
}
