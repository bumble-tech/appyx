package com.github.zsoltk.composeribs.core.routing

import kotlinx.coroutines.flow.StateFlow

interface RoutingSourceAdapter<Key, State> {

    val onScreen: StateFlow<RoutingElements<Key, out State>>

    val offScreen: StateFlow<RoutingElements<Key, out State>>
}