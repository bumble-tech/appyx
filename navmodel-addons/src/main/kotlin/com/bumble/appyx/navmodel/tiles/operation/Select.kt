package com.bumble.appyx.navmodel.tiles.operation

import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.core.navigation.RoutingKey
import com.bumble.appyx.navmodel.tiles.Tiles
import com.bumble.appyx.navmodel.tiles.TilesElements
import kotlinx.parcelize.Parcelize

@Parcelize
data class Select<T : Any>(
    private val key: RoutingKey<T>
) : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>
    ): RoutingElements<T, Tiles.TransitionState> =
        elements.map {
            if (it.key == key && it.targetState == Tiles.TransitionState.STANDARD) {
                it.transitionTo(
                    newTargetState = Tiles.TransitionState.SELECTED,
                    operation = this
                )
            } else {
                it
            }
        }
}

fun <T : Any> Tiles<T>.select(key: RoutingKey<T>) {
    accept(Select(key))
}
