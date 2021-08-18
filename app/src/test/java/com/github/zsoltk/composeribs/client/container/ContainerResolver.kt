package com.github.zsoltk.composeribs.client.container

import com.github.zsoltk.composeribs.client.container.Container.Routing
import com.github.zsoltk.composeribs.client.container.Container.Routing.*
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.Resolver

class ContainerResolver(
    private val builders: ContainerChildBuilders
) : Resolver<Routing> {

    override fun invoke(routing: Routing): Node<*> =
        with(builders) {
            when (routing) {
                Child1 -> child1Builder.build()
                Child2 -> child2Builder.build()
            }
        }
}
