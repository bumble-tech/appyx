package com.github.zsoltk.composeribs.core.children

import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.plugin.NodeAware
import kotlin.reflect.KClass

interface ChildAware : NodeAware {

    fun <T : Node> whenChildAttached(
        child: KClass<T>,
        callback: ChildCallback<T>,
    )

    fun <T1 : Node, T2 : Node> whenChildrenAttached(
        child1: KClass<T1>,
        child2: KClass<T2>,
        callback: ChildrenCallback<T1, T2>,
    )

}
