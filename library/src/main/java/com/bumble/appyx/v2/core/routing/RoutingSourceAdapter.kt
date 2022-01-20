package com.bumble.appyx.v2.core.routing

import kotlinx.coroutines.flow.StateFlow

interface RoutingSourceAdapter<Routing, State> {

    val onScreen: StateFlow<RoutingElements<Routing, out State>>

    val offScreen: StateFlow<RoutingElements<Routing, out State>>
}
