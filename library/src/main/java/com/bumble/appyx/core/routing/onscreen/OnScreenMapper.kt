package com.bumble.appyx.core.routing.onscreen

import com.bumble.appyx.core.routing.RoutingElements
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class OnScreenMapper<Routing, State>(
    private val scope: CoroutineScope,
    private val onScreenStateResolver: OnScreenStateResolver<State>
) {

    fun resolveOnScreenElements(state: StateFlow<RoutingElements<Routing, State>>): StateFlow<RoutingElements<Routing, State>> =
        state
            .map { elements ->
                elements.filter { element ->
                    onScreenStateResolver.isOnScreen(element)
                }
            }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    fun resolveOffScreenElements(state: StateFlow<RoutingElements<Routing, State>>): StateFlow<RoutingElements<Routing, State>> =
        state
            .map { elements ->
                elements.filterNot { element ->
                    onScreenStateResolver.isOnScreen(element)
                }
            }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

}
