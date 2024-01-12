package com.bumble.appyx.navigation.children

import com.bumble.appyx.navigation.node.AbstractNode

val <T> ChildEntry<T>.nodeOrNull: AbstractNode?
    get() =
        when (this) {
            is ChildEntry.Initialized -> node
            is ChildEntry.Suspended -> null
        }
