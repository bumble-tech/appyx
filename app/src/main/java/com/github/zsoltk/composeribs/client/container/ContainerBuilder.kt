package com.github.zsoltk.composeribs.client.container

import androidx.compose.animation.core.tween
import com.github.zsoltk.composeribs.client.container.Container.Routing
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.builder.SimpleBuilder
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.SubtreeController
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider

class ContainerBuilder(
    private val dependency: Container.Dependency
) : SimpleBuilder() {

    val builders = ContainerChildBuilders()

    override fun build(): Node<*> {
        val backStack = BackStack<Routing>(
            initialElement = Routing.Child(0)
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
                transitionHandler = BackStackSlider(
                    transitionSpec = { tween(1500) }
                )
            )
        )
    }
}
