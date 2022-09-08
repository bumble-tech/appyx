package com.bumble.appyx.core.children

import com.bumble.appyx.core.node.Node

val <T> ChildEntry<T>.nodeOrNull: Node?
    get() =
        when (this) {
            is ChildEntry.Initialized -> node
            is ChildEntry.Suspended -> null
        }
