package com.github.zsoltk.composeribs.core.routing

import kotlinx.coroutines.flow.StateFlow

interface RoutingSource<T, S> {

    val all: StateFlow<List<RoutingElement<T, S>>>

    val onScreen: StateFlow<List<RoutingElement<T, S>>>

    val offScreen: StateFlow<List<RoutingElement<T, S>>>

    val canHandleBackPress: StateFlow<Boolean>

    fun onBackPressed()

    fun onTransitionFinished(key: RoutingKey<T>)

}
