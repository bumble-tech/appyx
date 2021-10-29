package com.github.zsoltk.composeribs.core.children

import com.github.zsoltk.composeribs.core.Node

val <T> ChildEntry<T>.nodeOrNull: Node?
    get() =
        when (this) {
            is ChildEntry.Eager -> node
            is ChildEntry.Lazy -> null
        }
