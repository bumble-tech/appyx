package com.github.zsoltk.composeribs.core.routing.source.tiles.operation

import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles
import com.github.zsoltk.composeribs.core.routing.source.tiles.TilesElement
import com.github.zsoltk.composeribs.core.routing.source.tiles.TilesElements
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
            fromState = Tiles.TransitionState.CREATED,
            targetState = Tiles.TransitionState.STANDARD,
            operation = this
        )
}

fun <T : Any> Tiles<T>.add(element: T) {
    accept(Add(element))
}
