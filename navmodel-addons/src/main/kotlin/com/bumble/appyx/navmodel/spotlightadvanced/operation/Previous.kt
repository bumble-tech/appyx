package com.bumble.appyx.navmodel.spotlightadvanced.operation

import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState.Active
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState.Carousel
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState.InactiveAfter
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState.InactiveBefore
import kotlinx.parcelize.Parcelize


@Parcelize
class Previous<T : Any> : SpotlightAdvancedOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, TransitionState>) =
        elements.any {
            (it.fromState == InactiveBefore && it.targetState == InactiveBefore) ||
                    it.fromState is Carousel
        }

    override fun invoke(elements: RoutingElements<T, TransitionState>): RoutingElements<T, TransitionState> {
        if (elements.all { it.fromState is Carousel }) {
            return elements.map {
                when (val state = it.fromState) {
                    is Carousel -> {
                        val currentOffset = (it.fromState as Carousel).offset
                        val newOffset = currentOffset - 1
                        it.transitionTo(
                            newTargetState = state.copy(offset = newOffset),
                            operation = this
                        )
                    }
                    else -> {
                        it
                    }
                }

            }
        } else {
            val previousKey =
                elements.last { it.targetState == InactiveBefore }.key

            return elements.map {
                when {
                    it.targetState == Active -> {
                        it.transitionTo(
                            newTargetState = InactiveAfter,
                            operation = this
                        )
                    }
                    it.key == previousKey -> {
                        it.transitionTo(
                            newTargetState = Active,
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
}

fun <T : Any> SpotlightAdvanced<T>.previous() {
    accept(Previous())
}
