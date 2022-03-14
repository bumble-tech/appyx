package com.bumble.appyx.v2.core.routing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow


inline fun <R, S, reified P : RoutingPlugin<R, S>> BaseRoutingSource<R, S>.plugins(): List<P> =
    this.plugins.filterIsInstance(P::class.java)

interface RoutingPlugin<Routing, State> {

    fun init(routingSource: BaseRoutingSource<Routing, State>, scope: CoroutineScope)
}

interface BackPressHandler<Routing, State> : RoutingPlugin<Routing, State> {

    val canHandleBackPress: StateFlow<Boolean>

    fun onBackPressed()
}

interface OperationStrategy<Routing, State> : RoutingPlugin<Routing, State> {

    fun accept(operation: Operation<Routing, State>)
}
