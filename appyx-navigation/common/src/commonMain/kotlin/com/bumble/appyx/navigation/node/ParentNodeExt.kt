package com.bumble.appyx.navigation.node

import com.bumble.appyx.navigation.children.nodeOrNull

fun ParentNode<*>.children(): List<AbstractNode> {
    return children.value.values.mapNotNull { it.nodeOrNull }
}

inline fun <reified N : AbstractNode> ParentNode<*>.childrenOfType(): List<N> {
    return children.value.values.mapNotNull { it.nodeOrNull as? N }
}

inline fun <reified N : AbstractNode> ParentNode<*>.firstChildOfType(): N? {
    return children.value.values.firstOrNull { it.nodeOrNull is N }?.nodeOrNull as N?
}
