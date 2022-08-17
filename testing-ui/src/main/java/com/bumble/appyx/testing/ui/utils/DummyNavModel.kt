package com.bumble.appyx.testing.ui.utils

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver

class DummyNavModel<Routing : Any, State> : BaseNavModel<Routing, State>(
    savedStateMap = null,
    finalState = null,
    screenResolver = object : OnScreenStateResolver<State> {
        override fun isOnScreen(state: State) = true
    }
) {
    override val initialElements: RoutingElements<Routing, State>
        get() = emptyList()

}
