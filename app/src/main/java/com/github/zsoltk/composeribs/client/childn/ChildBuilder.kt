package com.github.zsoltk.composeribs.client.childn

import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.builder.Builder

class ChildBuilder(
    private val dependency: Child.Dependency
) : Builder<Int>() {

    override fun build(payload: Int): Node<*> = Node(
        view = ChildView(payload)
    )
}
