package com.bumble.appyx.core.children

import android.os.Parcelable
import com.bumble.appyx.core.node.Node

val <T : Parcelable> ChildEntry<T>.nodeOrNull: Node?
    get() =
        when (this) {
            is ChildEntry.Initialized -> node
            is ChildEntry.Suspended -> null
        }
