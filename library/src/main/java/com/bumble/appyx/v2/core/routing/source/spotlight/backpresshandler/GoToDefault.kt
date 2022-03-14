package com.bumble.appyx.v2.core.routing.source.spotlight.backpresshandler

import com.bumble.appyx.v2.core.routing.BackPressHandler
import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.activate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class GoToDefault<Routing : Any>(
    private val initialActiveIndex: Int = 0
) : BackPressHandler<Routing, Spotlight.TransitionState> {

    private lateinit var scope: CoroutineScope
    private lateinit var routingSource: Spotlight<Routing>

    override fun init(
        routingSource: BaseRoutingSource<Routing, Spotlight.TransitionState>,
        scope: CoroutineScope
    ) {
        this.scope = scope
        this.routingSource = routingSource as Spotlight<Routing>
    }

    override val canHandleBackPress: StateFlow<Boolean> by lazy {
        routingSource.state.map { elements ->
            elements.getOrNull(initialActiveIndex)?.targetState != ACTIVE
        }.stateIn(scope, SharingStarted.Eagerly, false)
    }

    override fun onBackPressed() {
        routingSource.activate(initialActiveIndex)
    }
}
