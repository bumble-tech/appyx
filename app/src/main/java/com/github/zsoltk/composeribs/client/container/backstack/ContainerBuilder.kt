package com.github.zsoltk.composeribs.client.container.backstack

import androidx.compose.animation.core.tween
import com.github.zsoltk.composeribs.client.container.backstack.ContainerNode.Routing
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.builder.SimpleBuilder
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.SubtreeController
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider

class ContainerBuilder() : SimpleBuilder() {

    override fun build(): Node<*> {
        val backStack = BackStack<Routing>(initialElement = Routing.Child(0))
        val composable = ContainerNode(backStack)
        val subtreeComposer = SubtreeController(
            routingSource = backStack,
            resolver = composable,
            transitionHandler = BackStackSlider(
                transitionSpec = { tween(1500) }
            )
        )

        return Node(
            composable = composable,
            subtreeController = subtreeComposer
        )
    }

//    fun buildVariant2(): Node<*> {
//        val tiles = Tiles(
//            initialElements = listOf(
//                Routing.Child1,
//                Routing.Child2,
//                Routing.Child3,
//                Routing.Child4,
//            )
//        )
//
//        val interactor = ContainerInteractorTiles(
//            tiles = tiles
//        )
//
//        val scTiles = SubtreeController(
//            routingSource = tiles,
//            resolver = ContainerResolver(),
//            transitionHandler = TilesTransitionHandler(
//                transitionSpec = { tween(1500) }
//            )
//        )
//
//        return Node(
//            view = ContainerTilesView(
//                onSelect = { interactor.select(it) },
//                onRemoveSelected = { interactor.removeSelected() }
//            ),
//            subtreeController = scTiles,
//        )
//    }
//
//    fun buildVariant3(): Node<*> {
//        val modal = Modal(
//            initialElements = emptyList<Routing>()
//        )
//
//        val interactor = ContainerInteractorModal(
//            modal = modal
//        )
//
//        val scTiles = SubtreeController(
//            routingSource = modal,
//            resolver = ContainerResolver(),
//            transitionHandler = ModalTransitionHandler(
//                transitionSpec = { tween(500) }
//            )
//        )
//
//        return Node(
//            view = ContainerModalView(
//                onShowModal = { interactor.showModal() },
//                onMakeFullScreen = { interactor.makeFullScreen() }
//            ),
//            subtreeController = scTiles,
//        )
//    }
}
