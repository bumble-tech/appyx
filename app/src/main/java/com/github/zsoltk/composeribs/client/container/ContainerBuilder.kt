package com.github.zsoltk.composeribs.client.container

import com.github.zsoltk.composeribs.client.container.Container.Routing
import com.github.zsoltk.composeribs.core.BackStack
import com.github.zsoltk.composeribs.core.Builder
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.SubtreeController

class ContainerBuilder(
    private val dependency: Container.Dependency
) : Builder() {

    val builders = ContainerChildBuilders()

    override fun build(): Node<*> {
        val backStack = BackStack<Routing>(
            initialElement = Routing.Child1
        )

        val interactor = ContainerInteractor(
            backStack = backStack
        )

        return Node(
            view = ContainerView(
                onPushRoutingClicked = { interactor.pushRouting() },
                onPopRoutingClicked = { interactor.popRouting() }
            ),
            subtreeController = SubtreeController(
                routingSource = backStack,
                resolver = ContainerResolver(builders),
                transitionHandler = ContainerTransitionHandler
            )
        )
    }
}
