package com.bumble.appyx.testing.view.utils

import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenStateResolver

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
