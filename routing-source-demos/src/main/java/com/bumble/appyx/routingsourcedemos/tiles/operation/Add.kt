package com.bumble.appyx.routingsourcedemos.tiles.operation

import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.routingsourcedemos.tiles.Tiles
import com.bumble.appyx.routingsourcedemos.tiles.TilesElement
import com.bumble.appyx.routingsourcedemos.tiles.TilesElements
import com.bumble.appyx.routingsourcedemos.tiles.Tiles.TransitionState.CREATED
import com.bumble.appyx.routingsourcedemos.tiles.Tiles.TransitionState.STANDARD
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Add<T : Any>(
    private val element: @RawValue T
) : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>,
    ): RoutingElements<T, Tiles.TransitionState> =
        elements + TilesElement(
            key = RoutingKey(element),
            fromState = CREATED,
            targetState = STANDARD,
            operation = this
        )
}

fun <T : Any> Tiles<T>.add(element: T) {
    accept(Add(element))
}
