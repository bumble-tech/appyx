package com.bumble.appyx.v2.core.routing.backpresshandlerstrategies

import com.bumble.appyx.v2.core.routing.RoutingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DontHandleBackPress<Routing, TransitionState, Source : RoutingSource<Routing, TransitionState>> :
    BaseBackPressHandlerStrategy<Routing, TransitionState, Source>() {

    override val canHandleBackPressFlow: Flow<Boolean> =
        flowOf(false)

    override fun onBackPressed() {
        // Noop
    }
}
