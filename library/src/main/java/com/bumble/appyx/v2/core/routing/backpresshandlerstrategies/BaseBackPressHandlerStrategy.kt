package com.bumble.appyx.v2.core.routing.backpresshandlerstrategies

import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class BaseBackPressHandlerStrategy<Routing, TransitionState>
    : BackPressHandlerStrategy<Routing, TransitionState> {

    protected lateinit var scope: CoroutineScope
    protected lateinit var routingSource: BaseRoutingSource<Routing, TransitionState>

    override fun init(
        routingSource: BaseRoutingSource<Routing, TransitionState>,
        scope: CoroutineScope
    ) {
        this.scope = scope
        this.routingSource = routingSource
    }

    protected abstract val canHandleBackPressFlow: Flow<Boolean>

    override val canHandleBackPress: StateFlow<Boolean> by lazy(LazyThreadSafetyMode.NONE) {
        canHandleBackPressFlow.stateIn(scope, SharingStarted.Eagerly, false)
    }
}
