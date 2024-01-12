package com.bumble.appyx.navigation.children

import com.bumble.appyx.navigation.node.AbstractNode

inline fun <reified T : AbstractNode> ChildAware<*>.whenChildAttached(
    noinline callback: ChildCallback<T>,
) {
    whenChildAttached(T::class, callback)
}

inline fun <reified T1 : AbstractNode, reified T2 : AbstractNode> ChildAware<*>.whenChildrenAttached(
    noinline callback: ChildrenCallback<T1, T2>,
) {
    whenChildrenAttached(T1::class, T2::class, callback)
}
