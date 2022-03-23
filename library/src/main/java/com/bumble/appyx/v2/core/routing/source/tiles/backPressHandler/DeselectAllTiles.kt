package com.bumble.appyx.v2.core.routing.source.tiles.backPressHandler

import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.source.tiles.Tiles
import com.bumble.appyx.v2.core.routing.source.tiles.TilesElements
import com.bumble.appyx.v2.core.routing.source.tiles.operation.DeselectAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeselectAllTiles<Routing : Any> :
    BaseBackPressHandlerStrategy<Routing, Tiles.TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        routingSource.elements.map(::areThereSelectedTiles)
    }

    private fun areThereSelectedTiles(elements: TilesElements<Routing>) =
        elements.any { it.targetState == Tiles.TransitionState.SELECTED }

    override fun onBackPressed() {
        routingSource.accept(DeselectAll())
    }
}
