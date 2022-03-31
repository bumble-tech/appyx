package com.bumble.appyx.v2.core.routing.source.spotlight.operations

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.INACTIVE_AFTER
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.INACTIVE_BEFORE
import com.bumble.appyx.v2.core.routing.source.spotlight.SpotlightElement
import com.bumble.appyx.v2.core.routing.source.spotlight.SpotlightElements
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class UpdateElements<T : Any>(
    private val items: @RawValue List<T>,
    private val initialActiveIndex: Int = 0,
) : SpotlightOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, TransitionState>) = true

    override fun invoke(elements: RoutingElements<T, TransitionState>): RoutingElements<T, TransitionState> {
        require(initialActiveIndex in items.indices) {
            "Initial active index $initialActiveIndex is out of bounds of provided list of items: ${items.indices}"
        }
        return items.toSpotlightElements(initialActiveIndex)
    }
}

fun <T : Any> Spotlight<T>.updateItems(
    items: List<T>,
    initialActiveItem: Int = 0
) {
    accept(UpdateElements(items, initialActiveItem))
}

internal fun <T> List<T>.toSpotlightElements(activeIndex: Int): SpotlightElements<T> =
    mapIndexed { index, item ->
        val state = when {
            index < activeIndex -> INACTIVE_BEFORE
            index == activeIndex -> ACTIVE
            else -> INACTIVE_AFTER
        }
        SpotlightElement(
            key = RoutingKey(item),
            fromState = state,
            targetState = state,
            operation = Operation.Noop()
        )
    }
