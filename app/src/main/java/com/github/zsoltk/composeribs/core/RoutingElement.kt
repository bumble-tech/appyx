package com.github.zsoltk.composeribs.core

data class RoutingElement<T, S>(
    val key: RoutingKey<T>,
    val targetState: S
)
