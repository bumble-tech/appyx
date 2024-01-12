package com.bumble.appyx.navigation.node

import com.bumble.appyx.navigation.children.nodeOrNull

fun Node<*>.children(): List<Node<*>> {
    return children.value.values.mapNotNull { it.nodeOrNull }
}

inline fun <reified N : Node<*>> Node<*>.childrenOfType(): List<N> {
    return children.value.values.mapNotNull { it.nodeOrNull as? N }
}

inline fun <reified N : Node<*>> Node<*>.firstChildOfType(): N? {
    return children.value.values.firstOrNull { it.nodeOrNull is N }?.nodeOrNull as N?
}
