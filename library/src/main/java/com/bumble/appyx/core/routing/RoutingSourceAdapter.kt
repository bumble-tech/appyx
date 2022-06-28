package com.bumble.appyx.core.routing

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface RoutingSourceAdapter<Routing, State> {

    val screenState: StateFlow<ScreenState<Routing, out State>>

    data class ScreenState<Routing, State>(
        val onScreen: RoutingElements<Routing, out State> = emptyList(),
        val offScreen: RoutingElements<Routing, out State> = emptyList(),
    )

}
