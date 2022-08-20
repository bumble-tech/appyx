package com.bumble.appyx.core.children

import com.bumble.appyx.core.node.Node

val <T> ChildEntry<T>.nodeOrNull: Node?
    get() =
        when (this) {
            is ChildEntry.Eager -> node
            is ChildEntry.Lazy -> null
        }
