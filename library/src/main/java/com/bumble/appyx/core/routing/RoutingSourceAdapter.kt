package com.bumble.appyx.core.routing

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface RoutingSourceAdapter<Routing, State> {

    val onScreen: StateFlow<RoutingElements<Routing, out State>>

    val offScreen: StateFlow<RoutingElements<Routing, out State>>
}
