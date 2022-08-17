package com.bumble.appyx.navmodel.spotlight.operation

import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.INACTIVE_AFTER
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.INACTIVE_BEFORE
import kotlinx.parcelize.Parcelize


@Parcelize
class Previous<T : Any> : SpotlightOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, Spotlight.TransitionState>) =
        elements.any { it.fromState == INACTIVE_BEFORE }

    override fun invoke(
        elements: RoutingElements<T, Spotlight.TransitionState>
    ): RoutingElements<T, Spotlight.TransitionState> {
        val previousKey =
            elements.last { it.targetState == INACTIVE_BEFORE }.key

        return elements.map {
            when {
                it.targetState == ACTIVE -> {
                    it.transitionTo(
                        newTargetState = INACTIVE_AFTER,
                        operation = this
                    )
                }
                it.key == previousKey -> {
                    it.transitionTo(
                        newTargetState = ACTIVE,
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

fun <T : Any> Spotlight<T>.previous() {
    accept(Previous())
}


