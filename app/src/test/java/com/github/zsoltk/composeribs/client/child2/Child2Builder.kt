package com.github.zsoltk.composeribs.client.child2

import com.github.zsoltk.composeribs.core.Builder
import com.github.zsoltk.composeribs.core.Node

class Child2Builder(
    private val dependency: Child2.Dependency
) : Builder() {

    val builders = Child2ChildBuilders()

    override fun build(): Node<*> = Node(
        view = Child2View()
    )
}
