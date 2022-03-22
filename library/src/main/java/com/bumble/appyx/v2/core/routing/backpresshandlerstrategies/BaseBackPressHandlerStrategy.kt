package com.bumble.appyx.v2.core.routing.backpresshandlerstrategies

import com.bumble.appyx.v2.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class BaseBackPressHandlerStrategy<Routing, TransitionState, Source : RoutingSource<Routing, TransitionState>>
    : BackPressHandlerStrategy<Routing, TransitionState, Source> {

    protected lateinit var scope: CoroutineScope
    protected lateinit var routingSource: Source

    override fun init(
        routingSource: Source,
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
