package com.bumble.appyx.navmodel.tiles.operation

import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.tiles.Tiles
import com.bumble.appyx.navmodel.tiles.TilesElements
import kotlinx.parcelize.Parcelize

@Parcelize
data class Destroy<T : Any>(
    private val key: NavKey<T>
) : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>
    ): RoutingElements<T, Tiles.TransitionState> =
        elements.map {
            if (it.key == key) {
                it.transitionTo(
                    newTargetState = Tiles.TransitionState.DESTROYED,
                    operation = this
                )
            } else {
                it
            }
        }
}

fun <T : Any> Tiles<T>.destroy(key: NavKey<T>) {
    accept(Destroy(key))
}
