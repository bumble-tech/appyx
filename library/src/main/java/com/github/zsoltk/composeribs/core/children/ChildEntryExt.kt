package com.github.zsoltk.composeribs.core.children

import com.github.zsoltk.composeribs.core.Node

val <T> ChildEntry<T>.nodeOrNull: Node<T>?
    get() =
        when (this) {
            is ChildEntry.Eager -> node as Node<T>
            is ChildEntry.Lazy -> null
        }
