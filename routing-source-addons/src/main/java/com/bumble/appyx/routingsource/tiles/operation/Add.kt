package com.bumble.appyx.routingsource.tiles.operation

import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.routingsource.tiles.Tiles
import com.bumble.appyx.routingsource.tiles.TilesElement
import com.bumble.appyx.routingsource.tiles.TilesElements
import com.bumble.appyx.routingsource.tiles.Tiles.TransitionState.CREATED
import com.bumble.appyx.routingsource.tiles.Tiles.TransitionState.STANDARD
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
