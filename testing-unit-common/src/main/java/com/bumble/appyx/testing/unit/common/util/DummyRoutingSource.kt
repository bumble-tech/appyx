package com.bumble.appyx.testing.unit.common.util

import com.bumble.appyx.core.routing.BaseRoutingSource
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.onscreen.OnScreenStateResolver

class DummyRoutingSource<Routing : Any, State> : BaseRoutingSource<Routing, State>(
    savedStateMap = null,
    finalState = null,
    screenResolver = object : OnScreenStateResolver<State> {
        override fun isOnScreen(state: State) = true
    }
) {
    override val initialElements: RoutingElements<Routing, State>
        get() = emptyList()

}
