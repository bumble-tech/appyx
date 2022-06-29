package com.bumble.appyx.core.routing.backpresshandlerstrategies

import com.bumble.appyx.core.routing.RoutingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DontHandleBackPress<Routing, TransitionState> :
    BaseBackPressHandlerStrategy<Routing, TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> =
        flowOf(false)

    override fun onBackPressed() {
        // Noop
    }
}
