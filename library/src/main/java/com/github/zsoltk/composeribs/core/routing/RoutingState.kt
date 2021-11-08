package com.github.zsoltk.composeribs.core.routing

data class RoutingState<Key, State>(
    val elements: RoutingElements<Key, State>,
    val operation: Operation<Key, State>
)
