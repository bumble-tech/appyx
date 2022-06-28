package com.bumble.appyx.core.routing

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface RoutingSourceAdapter<Routing, State> {

    val visibilityState: StateFlow<VisibilityState<Routing, out State>>

    val onScreen: StateFlow<RoutingElements<Routing, out State>>

    data class VisibilityState<Routing, State>(
        val onScreen: RoutingElements<Routing, out State> = emptyList(),
        val offScreen: RoutingElements<Routing, out State> = emptyList(),
    )

}
