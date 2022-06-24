package com.bumble.appyx.routingsourcedemos.spotlight.operation

import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.routingsourcedemos.spotlight.Spotlight
import com.bumble.appyx.routingsourcedemos.spotlight.Spotlight.TransitionState
import com.bumble.appyx.routingsourcedemos.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.routingsourcedemos.spotlight.Spotlight.TransitionState.INACTIVE_AFTER
import com.bumble.appyx.routingsourcedemos.spotlight.Spotlight.TransitionState.INACTIVE_BEFORE
import kotlinx.parcelize.Parcelize

@Parcelize
class Next<T : Any> : SpotlightOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, TransitionState>) =
        elements.any { it.fromState == INACTIVE_AFTER }

    override fun invoke(elements: RoutingElements<T, TransitionState>): RoutingElements<T, TransitionState> {
        val nextKey =
            elements.first { it.targetState == INACTIVE_AFTER }.key

        return elements.map {
            when {
                it.targetState == ACTIVE -> {
                    it.transitionTo(
                        targetState = INACTIVE_BEFORE,
                        operation = this
                    )
                }
                it.key == nextKey -> {
                    it.transitionTo(
                        targetState = ACTIVE,
                        operation = this
                    )
                }
                else -> {
                    it
                }
            }
        }
    }
}

fun <T : Any> Spotlight<T>.next() {
    accept(Next())
}

