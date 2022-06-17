package com.bumble.appyx.core.routing.source.spotlight.operation

import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.core.routing.source.spotlight.Spotlight.TransitionState
import com.bumble.appyx.core.routing.source.spotlight.currentIndex
import kotlinx.parcelize.Parcelize

@Parcelize
class Activate<T : Any>(
    private val index: Int
) : SpotlightOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, TransitionState>) =
        index != elements.currentIndex && index <= elements.lastIndex && index >= 0

    override fun invoke(elements: RoutingElements<T, TransitionState>): RoutingElements<T, TransitionState> {

        val toActivateIndex = this.index
        return elements.mapIndexed { index, element ->
            when {
                index < toActivateIndex -> {
                    element.transitionTo(
                        targetState = TransitionState.INACTIVE_BEFORE,
                        operation = this
                    )
                }
                index == toActivateIndex -> {
                    element.transitionTo(
                        targetState = TransitionState.ACTIVE,
                        operation = this
                    )
                }
                else -> {
                    element.transitionTo(
                        targetState = TransitionState.INACTIVE_AFTER,
                        operation = this
                    )
                }
            }
        }
    }
}


fun <T : Any> Spotlight<T>.activate(index: Int) {
    accept(Activate(index))
}
