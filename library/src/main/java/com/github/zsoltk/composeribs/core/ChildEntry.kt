package com.github.zsoltk.composeribs.core

import com.github.zsoltk.composeribs.core.routing.RoutingKey


data class NodeChildEntry<T>(
    val key: RoutingKey<T>,
    val node: Node<*>? = null,
    val onScreen: Boolean,
)
