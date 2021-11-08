package com.github.zsoltk.composeribs.core.routing.source.tiles.operation

import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles
import com.github.zsoltk.composeribs.core.routing.source.tiles.TilesElements
import com.github.zsoltk.composeribs.core.routing.source.tiles.TilesOperation

class DeselectAll<T : Any> : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>,
        uuidGenerator: UuidGenerator
    ): RoutingElements<T, Tiles.TransitionState> =
        elements.map {
            if (it.targetState == Tiles.TransitionState.SELECTED) {
                it.copy(targetState = Tiles.TransitionState.STANDARD)
            } else {
                it
            }
        }
}

fun <T : Any> Tiles<T>.deselectAll() {
    perform(DeselectAll())
}
