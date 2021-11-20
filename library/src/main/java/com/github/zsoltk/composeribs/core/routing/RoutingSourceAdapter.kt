package com.github.zsoltk.composeribs.core.routing

import kotlinx.coroutines.flow.StateFlow

interface RoutingSourceAdapter<Key, out State> {

    val elements: StateFlow<RoutingElements<Key, out State>>

    val onScreen: StateFlow<RoutingElements<Key, out State>>

    val offScreen: StateFlow<RoutingElements<Key, out State>>

    fun onTransitionFinished(key: RoutingKey<Key>)

    fun isElementOnScreen(key: RoutingKey<Key>): Boolean
}