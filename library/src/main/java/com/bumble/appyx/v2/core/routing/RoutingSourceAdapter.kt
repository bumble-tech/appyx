package com.bumble.appyx.v2.core.routing

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface RoutingSourceAdapter<Routing, State> {

    val onScreen: StateFlow<RoutingElements<Routing, out State>>

    val offScreen: StateFlow<RoutingElements<Routing, out State>>
}
