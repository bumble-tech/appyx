package com.github.zsoltk.composeribs.core


data class NodeChildEntry<T>(
    val key: RoutingKey<T>,
    val node: Node<*>? = null,
    val onScreen: Boolean,
)
