package com.github.zsoltk.composeribs.client.child2

import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.builder.SimpleBuilder

class Child2Builder(
    private val dependency: Child2.Dependency
) : SimpleBuilder() {

    val builders = Child2ChildBuilders()

    override fun build(): Node<*> = Node(
        view = Child2View()
    )
}
