package com.bumble.appyx.v2.core.children

import com.bumble.appyx.v2.core.node.Node

val <T> ChildEntry<T>.nodeOrNull: Node?
    get() =
        when (this) {
            is ChildEntry.Eager -> node
            is ChildEntry.Lazy -> null
        }
