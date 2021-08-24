package com.github.zsoltk.composeribs.client.container

import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles

class ContainerInteractorTiles(
    private val tiles: Tiles<Container.Routing>
) {

    fun select(routing: RoutingKey<Container.Routing>) {
        tiles.toggleSelection(routing)
    }

    fun removeSelected() {
        tiles.removeSelected()
    }
}
