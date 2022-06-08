package com.bumble.appyx.routingsourcedemos.tiles.operation

import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.routingsourcedemos.tiles.Tiles
import com.bumble.appyx.routingsourcedemos.tiles.TilesElements
import kotlinx.parcelize.Parcelize

@Parcelize
data class ToggleSelection<T : Any>(
    private val key: RoutingKey<T>
) : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>
    ): RoutingElements<T, Tiles.TransitionState> =
        elements.map {
            if (it.key == key) {
                when (it.targetState) {
                    Tiles.TransitionState.SELECTED -> it.transitionTo(
                        targetState = Tiles.TransitionState.STANDARD,
                        operation = this
                    )
                    Tiles.TransitionState.STANDARD -> it.transitionTo(
                        targetState = Tiles.TransitionState.SELECTED,
                        operation = this
                    )
                    else -> it
                }
            } else {
                it
            }
        }
}

fun <T : Any> Tiles<T>.toggleSelection(key: RoutingKey<T>) {
    accept(ToggleSelection(key))
}
