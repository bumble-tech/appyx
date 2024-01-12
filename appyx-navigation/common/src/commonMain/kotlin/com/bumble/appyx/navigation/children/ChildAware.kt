package com.bumble.appyx.navigation.children

import com.bumble.appyx.navigation.node.AbstractNode
import com.bumble.appyx.navigation.plugin.NodeAware
import kotlin.reflect.KClass

interface ChildAware<N: AbstractNode> : NodeAware<N> {

    fun <T : Any> whenChildAttached(
        child: KClass<T>,
        callback: ChildCallback<T>,
    )

    fun <T1 : Any, T2 : Any> whenChildrenAttached(
        child1: KClass<T1>,
        child2: KClass<T2>,
        callback: ChildrenCallback<T1, T2>,
    )

}
