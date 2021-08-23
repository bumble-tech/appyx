package com.github.zsoltk.composeribs.client.child1

import com.github.zsoltk.composeribs.core.builder.SimpleBuilder
import com.github.zsoltk.composeribs.core.Node

class Child1Builder(
    private val dependency: Child1.Dependency
) : SimpleBuilder() {

    val builders = Child1ChildBuilders()

    override fun build(): Node<*> = Node(
        view = Child1View()
    )
}
