package com.bumble.appyx.v2.core.routing.source.backstack.backpressHandler

import com.bumble.appyx.v2.core.routing.BackPressHandler
import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.operation.pop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PopBackPressHandler<Routing : Any> : BackPressHandler<Routing, BackStack.TransitionState> {

    private lateinit var scope: CoroutineScope
    private lateinit var routingSource: BackStack<Routing>

    override fun init(
        routingSource: BaseRoutingSource<Routing, BackStack.TransitionState>,
        scope: CoroutineScope
    ) {
        this.scope = scope
        this.routingSource = routingSource as BackStack<Routing>
    }

    override val canHandleBackPress: StateFlow<Boolean> by lazy {
        routingSource.state.map { list -> list.count { it.targetState == BackStack.TransitionState.STASHED_IN_BACK_STACK } > 0 }
            .stateIn(scope, SharingStarted.Eagerly, false)
    }

    override fun onBackPressed() {
        routingSource.pop()
    }
}
