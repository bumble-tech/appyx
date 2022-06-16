package com.bumble.appyx.core.routing.source.tiles.operation

import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.source.tiles.Tiles
import com.bumble.appyx.core.routing.source.tiles.TilesElements
import kotlinx.parcelize.Parcelize

@Parcelize
data class Destroy<T : Any>(
    private val key: RoutingKey<T>
) : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>
    ): RoutingElements<T, Tiles.TransitionState> =
        elements.map {
            if (it.key == key) {
                it.transitionTo(
                    targetState = Tiles.TransitionState.DESTROYED,
                    operation = this
                )
            } else {
                it
            }
        }
}

fun <T : Any> Tiles<T>.destroy(key: RoutingKey<T>) {
    accept(Destroy(key))
}
