package com.github.zsoltk.composeribs.client.container

import androidx.compose.animation.core.tween
import com.github.zsoltk.composeribs.client.container.Container.Routing
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.builder.SimpleBuilder
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.SubtreeController
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider
import com.github.zsoltk.composeribs.core.routing.source.modal.Modal
import com.github.zsoltk.composeribs.core.routing.source.modal.ModalTransitionHandler
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles
import com.github.zsoltk.composeribs.core.routing.source.tiles.TilesTransitionHandler

class ContainerBuilder(
    private val dependency: Container.Dependency
) : SimpleBuilder() {

    val builders = ContainerChildBuilders()

    override fun build(): Node<*> =
        buildVariant3()

    fun buildVariant1(): Node<*> {
        val backStack = BackStack<Routing>(
            initialElement = Routing.Child(0)
        )


        val interactor = ContainerInteractor(
            backStack = backStack
        )

        val scBackStack = SubtreeController(
            routingSource = backStack,
            resolver = ContainerResolver(builders),
            transitionHandler = BackStackSlider(
                transitionSpec = { tween(1500) }
            )
        )

        return Node(
            view = ContainerView(
                onPushRoutingClicked = { interactor.pushRouting() },
                onPopRoutingClicked = { interactor.popRouting() }
            ),
            subtreeController = scBackStack
        )
    }

    fun buildVariant2(): Node<*> {
        val tiles = Tiles(
            initialElements = listOf(
                Routing.Child1,
                Routing.Child2,
                Routing.Child3,
                Routing.Child4,
            )
        )

        val interactor = ContainerInteractorTiles(
            tiles = tiles
        )

        val scTiles = SubtreeController(
            routingSource = tiles,
            resolver = ContainerResolver(builders),
            transitionHandler = TilesTransitionHandler(
                transitionSpec = { tween(1500) }
            )
        )

        return Node(
            view = ContainerTilesView(
                onSelect = { interactor.select(it) },
                onRemoveSelected = { interactor.removeSelected() }
            ),
            subtreeController = scTiles,
        )
    }

    fun buildVariant3(): Node<*> {
        val modal = Modal(
            initialElements = emptyList<Routing>()
        )

        val interactor = ContainerInteractorModal(
            modal = modal
        )

        val scTiles = SubtreeController(
            routingSource = modal,
            resolver = ContainerResolver(builders),
            transitionHandler = ModalTransitionHandler(
                transitionSpec = { tween(500) }
            )
        )

        return Node(
            view = ContainerModalView(
                onShowModal = { interactor.showModal() },
                onMakeFullScreen = { interactor.makeFullScreen() }
            ),
            subtreeController = scTiles,
        )
    }
}
