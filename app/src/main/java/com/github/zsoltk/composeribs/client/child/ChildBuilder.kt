package com.github.zsoltk.composeribs.client.child

import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.builder.Builder

class ChildBuilder : Builder<Int>() {

    override fun build(payload: Int): Node<*> = Node(
        composable = ChildView(payload)
    )
}
