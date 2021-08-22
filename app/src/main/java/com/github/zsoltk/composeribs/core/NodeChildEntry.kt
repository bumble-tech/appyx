package com.github.zsoltk.composeribs.core


data class NodeChildEntry<T>(
    val routing: T,
    val node: Node<*>? = null,
    val onScreen: Boolean,
)
