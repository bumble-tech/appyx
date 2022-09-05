package com.bumble.appyx.core.node

import com.bumble.appyx.core.children.nodeOrNull

fun ParentNode<*>.children(): List<Node> {
    return this.children.value.values.mapNotNull { it.nodeOrNull }
}

inline fun <reified N : Node> ParentNode<*>.childrenOfType(): List<N> {
    return this.children.value.values.mapNotNull { it.nodeOrNull }.filterIsInstance<N>()
}

inline fun <reified N : Node> ParentNode<*>.firstChildOfType(): N {
    return childrenOfType<N>().first()
}
