package com.bumble.appyx.navmodel.spotlightadvanced.operation

import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState
import kotlinx.parcelize.Parcelize

@Parcelize
class SwitchToPager<T : Any> : SpotlightAdvancedOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, TransitionState>) =
        elements.all { it.fromState is TransitionState.Carousel }

    override fun invoke(elements: RoutingElements<T, TransitionState>): RoutingElements<T, TransitionState> {

        val activeIndex =
            elements.indexOfFirst {
                val state = it.targetState as TransitionState.Carousel
                (state.offset % state.max) == 0
            }

        return elements.mapIndexed { index, element ->
            val state = if (index < activeIndex) {
                TransitionState.InactiveBefore
            } else if (index == activeIndex) {
                TransitionState.Active
            } else {
                TransitionState.InactiveAfter
            }
            element.transitionTo(
                newTargetState = state,
                operation = this
            )
        }
    }
}


fun <T : Any> SpotlightAdvanced<T>.switchToPager() {
    accept(SwitchToPager())
}
