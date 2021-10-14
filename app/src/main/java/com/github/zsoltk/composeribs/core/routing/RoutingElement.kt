package com.github.zsoltk.composeribs.core.routing

data class RoutingElement<T, S>(
    val key: RoutingKey<T>,
    val fromState: S,
    val targetState: S,
    // TODO Should be calculated from targetState
    val onScreen: Boolean,
)
