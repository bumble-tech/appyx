package com.bumble.appyx.core.routing.onscreen

import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingSourceAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class OnScreenMapper<Routing, State>(
    private val scope: CoroutineScope,
    private val onScreenStateResolver: OnScreenStateResolver<State>
) {

    fun resolveVisibilityState(
        state: Flow<RoutingElements<Routing, out State>>,
    ): StateFlow<RoutingSourceAdapter.VisibilityState<Routing, State>> =
        state
            .map { elements ->
                RoutingSourceAdapter.VisibilityState(
                    onScreen = elements.filter { onScreenStateResolver.isOnScreen(it) },
                    offScreen = elements.filterNot { onScreenStateResolver.isOnScreen(it) },
                )
            }
            .stateIn(scope, SharingStarted.Eagerly, RoutingSourceAdapter.VisibilityState())

    fun resolveOnScreenElements(
        state: Flow<RoutingSourceAdapter.VisibilityState<Routing, out State>>,
    ): StateFlow<RoutingElements<Routing, out State>> =
        state.map { it.onScreen }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    fun resolveOffScreenElements(
        state: Flow<RoutingSourceAdapter.VisibilityState<Routing, out State>>,
    ): StateFlow<RoutingElements<Routing, out State>> =
        state.map { it.offScreen }.stateIn(scope, SharingStarted.Eagerly, emptyList())

}
