package com.github.zsoltk.composeribs.client.container

import com.github.zsoltk.composeribs.client.container.Container.Routing
import com.github.zsoltk.composeribs.client.container.Container.Routing.*
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.Resolver

class ContainerResolver(
    private val builders: ContainerChildBuilders
) : Resolver<Routing> {

    override fun invoke(routing: Routing): Node<*> =
        with(builders) {
            when (routing) {
                is Child -> childBuilder.build(routing.i)
//                is Child1 -> child1Builder.build()
//                is Child2 -> child2Builder.build()
                is Child1 -> childBuilder.build(1)
                is Child2 -> childBuilder.build(2)
                is Child3 -> childBuilder.build(3)
                is Child4 -> childBuilder.build(4)
            }
        }
}
