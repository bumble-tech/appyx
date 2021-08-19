package com.github.zsoltk.composeribs.core

data class ChildEntry<T>(
    val routing: T,
    val node: Node<*>? = null,
    val onScreen: Boolean,
)
