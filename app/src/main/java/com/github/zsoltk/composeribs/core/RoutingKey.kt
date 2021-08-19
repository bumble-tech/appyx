package com.github.zsoltk.composeribs.core

data class RoutingKey<T>(
    val id: Int, // TODO Int -> Any
    val routing: T
)
