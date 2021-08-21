package com.github.zsoltk.composeribs.core

data class RoutingElement<T, S>(
    val routingKey: RoutingKey<T>,
    val targetState: S
)
