package com.bumble.appyx.navigation.children

import com.bumble.appyx.navigation.node.Node

val <T> ChildEntry<T>.nodeOrNull: Node<*>?
    get() =
        when (this) {
            is ChildEntry.Initialized -> node
            is ChildEntry.Suspended -> null
        }
