package com.github.zsoltk.composeribs.core.routing

data class RoutingElement<T, S>(
    val key: RoutingKey<T>,
    val fromState: S,
    val targetState: S,
    val onScreen: Boolean
)
