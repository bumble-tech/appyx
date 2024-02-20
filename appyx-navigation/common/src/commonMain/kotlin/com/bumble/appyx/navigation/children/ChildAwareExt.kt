package com.bumble.appyx.navigation.children

import com.bumble.appyx.navigation.node.Node

inline fun <reified T : Node<*>> ChildAware<*>.whenChildAttached(
    noinline callback: ChildCallback<T>,
) {
    whenChildAttached(T::class, callback)
}

inline fun <reified T1 : Node<*>, reified T2 : Node<*>> ChildAware<*>.whenChildrenAttached(
    noinline callback: ChildrenCallback<T1, T2>,
) {
    whenChildrenAttached(T1::class, T2::class, callback)
}
